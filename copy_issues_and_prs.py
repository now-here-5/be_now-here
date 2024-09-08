import requests
import os
import time  # 지연 시간을 추가하기 위해 time 모듈 사용

# GitHub 토큰과 레포지토리 정보
GITHUB_TOKEN = os.getenv('GITHUB_TOKEN')
PRIVATE_REPO = os.getenv('PRIVATE_REPO')
PUBLIC_REPO = os.getenv('PUBLIC_REPO')

headers = {
    'Authorization': f'token {GITHUB_TOKEN}',
    'Accept': 'application/vnd.github.v3+json'
}

# 레포지토리의 모든 이슈 및 PR을 가져옴 (open/closed/merged 상태 모두 포함)
def get_all_issues_and_prs(repo):
    url = f'https://api.github.com/repos/{repo}/issues?state=all'
    response = requests.get(url, headers=headers)
    print(f"Fetching issues/PRs from {repo}, Status Code: {response.status_code}")
    try:
        response.raise_for_status()
        issues_and_prs = response.json()
        # 이슈와 PR을 created_at 시간에 따라 정렬 (오름차순)
        return sorted(issues_and_prs, key=lambda x: x['created_at'])
    except requests.exceptions.HTTPError as e:
        print(f"HTTP error occurred when fetching issues/PRs: {e}")
    return None

# 목적지 레포지토리의 기존 이슈 및 PR 목록을 가져옴
def get_existing_issues_and_prs(repo):
    return get_all_issues_and_prs(repo)

# 이슈 또는 PR이 이미 존재하는지 확인 (제목 기준 비교)
def item_exists_in_repo(item, existing_items, is_pr=False):
    # PR인 경우, [PR] 태그 없이 원래 제목으로 비교
    title_to_compare = item['title']
    if is_pr:
        title_to_compare = f"[PR] {title_to_compare}"
    
    for existing_item in existing_items:
        # [PR]이 붙기 전의 제목으로 비교
        if title_to_compare == existing_item.get('title'):
            return True
    return False

# 새로운 이슈 복사 (PR을 이슈로 변환)
def create_issue(repo, issue, is_pr=False):
    title = issue.get('title', 'No title')
    if is_pr:
        title = f"[PR] {title}"  # PR을 이슈로 변환할 때 제목에 [PR] 추가
    
    url = f'https://api.github.com/repos/{repo}/issues'
    data = {
        'title': title,
        'body': issue.get('body', ''),
        'labels': [label['name'] for label in issue.get('labels', [])],
        'state': issue.get('state', 'open')  # 기본값 'open'으로 설정
    }
    response = requests.post(url, json=data, headers=headers)
    if response.status_code == 201:
        return response.json()
    else:
        print(f"Failed to create issue: {response.status_code}, {response.text}")
        return None

# 모든 새로운 이슈 및 PR을 이슈로 복사 (생성 시간 순으로)
def copy_new_issues_and_prs():
    existing_items = get_existing_issues_and_prs(PUBLIC_REPO)  # 목적지 레포지토리의 기존 이슈 및 PR
    
    if existing_items is None:
        print("No existing issues/PRs found or failed to fetch.")
        existing_items = []  # 비어 있는 리스트로 설정

    issues_and_prs = get_all_issues_and_prs(PRIVATE_REPO)  # 원본 레포지토리의 이슈 및 PR
    
    if issues_and_prs:
        for item in issues_and_prs:
            # PR일 경우 이슈로 변환하여 [PR]을 제목에 추가
            if 'pull_request' in item:
                if not item_exists_in_repo(item, existing_items, is_pr=True):
                    new_issue = create_issue(PUBLIC_REPO, item, is_pr=True)
                    if new_issue:
                        print(f"Pull Request {item.get('title', 'No title')} copied to {PUBLIC_REPO} as issue #{new_issue.get('number')}")
                    time.sleep(1)  # 각 PR 복사 후 1초 지연
                else:
                    print(f"Pull Request {item.get('title', 'No title')} already exists in {PUBLIC_REPO}. Skipping.")
            else:  # 일반 이슈인 경우
                if not item_exists_in_repo(item, existing_items):
                    new_issue = create_issue(PUBLIC_REPO, item)
                    if new_issue:
                        print(f"Issue {item.get('title', 'No title')} copied to {PUBLIC_REPO} as issue #{new_issue.get('number')}")
                    time.sleep(1)  # 각 이슈 복사 후 1초 지연
                else:
                    print(f"Issue {item.get('title', 'No title')} already exists in {PUBLIC_REPO}. Skipping.")

# 함수 실행
copy_new_issues_and_prs()
