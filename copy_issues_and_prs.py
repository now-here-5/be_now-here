import requests
import os

# GitHub 토큰과 레포지토리 정보
GITHUB_TOKEN = os.getenv('GITHUB_TOKEN')  # GitHub Actions에서는 secrets.GITHUB_TOKEN 설정 필요
PRIVATE_REPO = os.getenv('PRIVATE_REPO')  # 원본 레포지토리
PUBLIC_REPO = os.getenv('PUBLIC_REPO')    # 목적지 레포지토리

headers = {
    'Authorization': f'token {GITHUB_TOKEN}',
    'Accept': 'application/vnd.github.v3+json'
}

# 레포지토리의 모든 이슈 및 PR을 가져옴 (open/closed/merged 상태 모두 포함)
def get_all_issues_and_prs(repo):
    url = f'https://api.github.com/repos/{repo}/issues?state=all'
    response = requests.get(url, headers=headers)
    
    try:
        response.raise_for_status()
        return response.json()
    except requests.exceptions.HTTPError as e:
        print(f"HTTP error occurred when fetching issues/PRs: {e}")
    return None

# 목적지 레포지토리의 기존 이슈 및 PR 목록을 가져옴
def get_existing_issues_and_prs(repo):
    return get_all_issues_and_prs(repo)

# 이슈 또는 PR이 이미 존재하는지 확인 (제목 기준 비교)
def item_exists_in_repo(item, existing_items):
    for existing_item in existing_items:
        if item['title'] == existing_item.get('title'):
            return True
    return False

# 새로운 이슈 복사
def create_issue(repo, issue):
    url = f'https://api.github.com/repos/{repo}/issues'
    data = {
        'title': issue.get('title', 'No title'),
        'body': issue.get('body', ''),
        'labels': [label['name'] for label in issue.get('labels', [])],
        'state': issue.get('state', 'open')  # 기본값 'open'으로 설정, 필요시 수정
    }
    response = requests.post(url, json=data, headers=headers)
    if response.status_code == 201:
        return response.json()
    else:
        print(f"Failed to create issue: {response.status_code}, {response.text}")
    return None

# PR 복사 (브랜치 정보가 없을 경우에도 복사 가능하도록 수정)
def create_pull_request(repo, issue):
    if 'pull_request' in issue:
        pr_url = issue['pull_request']['url']
        pr_number = pr_url.split('/')[-1]  # PR 번호 추출
        pr_details = get_pull_request_details(PRIVATE_REPO, pr_number)  # 원본 레포지토리에서 PR 가져오기
        
        if pr_details:
            head_branch = pr_details.get('head', {}).get('ref', None)  # head 브랜치가 없을 경우 None
            base_branch = pr_details.get('base', {}).get('ref', None)  # base 브랜치가 없을 경우 None
            
            # 브랜치 정보가 없는 경우에도 복사 가능하도록 처리
            data = {
                'title': issue.get('title', 'No title'),
                'body': issue.get('body', ''),
                # head와 base 브랜치가 있는 경우에만 포함
                'head': head_branch if head_branch else 'default-head-branch',  # 기본 head 브랜치
                'base': base_branch if base_branch else 'main'  # 기본 base 브랜치 (필요시 변경 가능)
            }
            url = f'https://api.github.com/repos/{repo}/pulls'
            response = requests.post(url, json=data, headers=headers)
            if response.status_code == 201:
                return response.json()
            else:
                print(f"Failed to create pull request: {response.status_code}, {response.text}")
        else:
            print(f"Failed to fetch pull request details for PR #{pr_number}.")
    return None

# 모든 새로운 이슈 및 PR 복사
def copy_new_issues_and_prs():
    existing_items = get_existing_issues_and_prs(PUBLIC_REPO)  # 목적지 레포지토리의 기존 이슈 및 PR
    
    if not existing_items:
        print("Failed to fetch existing issues/PRs from the target repository.")
        return

    issues_and_prs = get_all_issues_and_prs(PRIVATE_REPO)  # 원본 레포지토리의 이슈 및 PR
    
    if issues_and_prs:
        for item in issues_and_prs:
            if 'pull_request' not in item:  # 일반 이슈인 경우
                if not item_exists_in_repo(item, existing_items):
                    new_issue = create_issue(PUBLIC_REPO, item)
                    if new_issue:
                        print(f"Issue {item.get('title', 'No title')} copied to {PUBLIC_REPO} as #{new_issue.get('number')}")
                else:
                    print(f"Issue {item.get('title', 'No title')} already exists in {PUBLIC_REPO}. Skipping.")
            else:  # PR인 경우
                if not item_exists_in_repo(item, existing_items):
                    new_pr = create_pull_request(PUBLIC_REPO, item)
                    if new_pr:
                        print(f"Pull Request {item.get('title', 'No title')} copied to {PUBLIC_REPO} as #{new_pr.get('number')}")
                else:
                    print(f"Pull Request {item.get('title', 'No title')} already exists in {PUBLIC_REPO}. Skipping.")

# 함수 실행
copy_new_issues_and_prs()
