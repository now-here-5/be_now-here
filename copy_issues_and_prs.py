import os
import requests
import time

# GitHub 토큰을 환경 변수에서 가져오기 (보안 향상)
GITHUB_TOKEN = os.getenv('GITHUB_TOKEN')  # GitHub Actions에서는 secrets.GITHUB_TOKEN 설정 필요
PRIVATE_REPO = 'now-here-5/be_now-here'
PUBLIC_REPO = 'now-here-5/Now-Here'

headers = {
    'Authorization': f'token {GITHUB_TOKEN}',
    'Accept': 'application/vnd.github.v3+json'
}

# Rate Limit 확인 함수
def check_rate_limit():
    rate_limit_url = 'https://api.github.com/rate_limit'
    response = requests.get(rate_limit_url, headers=headers)
    
    if response.status_code == 200:
        rate_limit_info = response.json()
        remaining = rate_limit_info['resources']['core']['remaining']
        reset_time = rate_limit_info['resources']['core']['reset']
        print(f"Remaining requests: {remaining}, Reset time (epoch): {reset_time}")
        
        if remaining == 0:
            wait_time = max(reset_time - int(time.time()), 0)
            print(f"Rate limit reached. Sleeping for {wait_time} seconds.")
            time.sleep(wait_time)

# 이슈 및 PR 가져오기
def get_issues_and_prs(repo):
    url = f'https://api.github.com/repos/{repo}/issues?state=all'
    check_rate_limit()  # Rate limit 체크
    response = requests.get(url, headers=headers)
    
    try:
        response.raise_for_status()  # 응답 상태 코드 확인 (200 OK 여부)
        return response.json()  # 성공 시 JSON 반환
    except requests.exceptions.HTTPError as e:
        print(f"HTTP error occurred: {e}")
    except ValueError:
        print("Error: Received non-JSON response")
    return None

# 특정 PR의 상세 정보를 가져오기
def get_pull_request_details(repo, pull_number):
    """PR 번호를 통해 PR의 head와 base 브랜치 정보를 가져옵니다."""
    url = f'https://api.github.com/repos/{repo}/pulls/{pull_number}'
    response = requests.get(url, headers=headers)
    
    try:
        response.raise_for_status()
        return response.json()
    except requests.exceptions.HTTPError as e:
        print(f"HTTP error occurred when getting PR details: {e}")
    return None

# 이슈 생성
def create_issue(repo, issue):
    check_rate_limit()  # Rate limit 체크
    if isinstance(issue, dict):
        url = f'https://api.github.com/repos/{repo}/issues'
        data = {
            'title': issue.get('title', 'No title'),
            'body': issue.get('body', ''),
            'labels': [label['name'] for label in issue.get('labels', [])],
            'state': issue.get('state', 'open')  # 기본값 'open'으로 설정
        }
        response = requests.post(url, json=data, headers=headers)
        if response.status_code == 201:
            return response.json()
        else:
            print(f"Failed to create issue: {response.status_code}, {response.text}")
    else:
        print("Error: issue is not a dictionary")
    return None

# Pull Request 생성
def create_pull_request(repo, issue):
    """PR의 head와 base 브랜치를 가져와서 PR을 복사합니다."""
    check_rate_limit()  # Rate limit 체크
    if isinstance(issue, dict) and 'pull_request' in issue:
        # 이슈에서 PR 번호를 가져옴
        pr_url = issue.get('pull_request', {}).get('url')
        if pr_url:
            pr_number = pr_url.split('/')[-1]  # URL에서 pull_number 추출
            pr_details = get_pull_request_details(repo, pr_number)
            
            if pr_details:
                head_branch = pr_details.get('head', {}).get('ref')
                base_branch = pr_details.get('base', {}).get('ref')

                # head 또는 base 브랜치 정보가 없을 경우 오류 출력
                if not head_branch or not base_branch:
                    print(f"Error: head or base branch is invalid. Head: {head_branch}, Base: {base_branch}")
                    return None

                # PR 생성 요청
                url = f'https://api.github.com/repos/{repo}/pulls'
                data = {
                    'title': issue.get('title', 'No title'),
                    'body': issue.get('body', ''),
                    'head': head_branch,
                    'base': base_branch
                }
                response = requests.post(url, json=data, headers=headers)
                if response.status_code == 201:
                    return response.json()
                else:
                    print(f"Failed to create pull request: {response.status_code}, {response.text}")
            else:
                print("Failed to fetch pull request details.")
        else:
            print("No valid pull_request URL found in the issue.")
    else:
        print("Error: issue is not a dictionary or does not contain a pull_request field.")
    return None

# 이슈 및 PR 복사 실행
issues = get_issues_and_prs(PRIVATE_REPO)
if issues:
    for issue in issues:
        if 'pull_request' not in issue:  # 일반 이슈인 경우
            new_issue = create_issue(PUBLIC_REPO, issue)
            if new_issue:
                print(f"Issue {issue.get('title', 'No title')} copied to {PUBLIC_REPO} as #{new_issue.get('number')}")
        else:  # Pull Request인 경우
            new_pr = create_pull_request(PUBLIC_REPO, issue)
            if new_pr:
                print(f"Pull Request {issue.get('title', 'No title')} copied to {PUBLIC_REPO} as #{new_pr.get('number')}")
