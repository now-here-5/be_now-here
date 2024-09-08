import requests

GITHUB_TOKEN = 'your_github_token_here'
PRIVATE_REPO = 'now-here-5/be_now-here'
PUBLIC_REPO = 'now-here-5/Now-Here'

headers = {
    'Authorization': f'token {GITHUB_TOKEN}',
    'Accept': 'application/vnd.github.v3+json'
}

def get_issues_and_prs(repo):
    url = f'https://api.github.com/repos/{repo}/issues?state=all'
    response = requests.get(url, headers=headers)
    
    # 응답이 JSON인지 확인
    try:
        response.raise_for_status()  # API 요청이 성공했는지 확인
        return response.json()  # JSON 응답을 파싱하여 반환
    except ValueError:
        print("Error: Received non-JSON response")
        return None
    except requests.exceptions.HTTPError as e:
        print(f"HTTP error occurred: {e}")
        return None

def create_issue(repo, issue):
    # issue 변수가 제대로 딕셔너리로 전달되는지 확인
    if isinstance(issue, dict):
        url = f'https://api.github.com/repos/{repo}/issues'
        data = {
            'title': issue['title'],
            'body': issue['body'],
            'labels': [label['name'] for label in issue.get('labels', [])],
            'state': issue['state']
        }
        response = requests.post(url, json=data, headers=headers)
        return response.json()
    else:
        print("Error: issue is not a dictionary")
        return None

def create_pull_request(repo, issue):
    if isinstance(issue, dict):
        url = f'https://api.github.com/repos/{repo}/pulls'
        data = {
            'title': issue['title'],
            'body': issue['body'],
            'head': 'source_branch',  # 기존 PR의 head 브랜치 이름으로 대체
            'base': 'target_branch',  # 기존 PR의 base 브랜치 이름으로 대체
            'state': issue['state']
        }
        response = requests.post(url, json=data, headers=headers)
        return response.json()
    else:
        print("Error: issue is not a dictionary")
        return None

issues = get_issues_and_prs(PRIVATE_REPO)
if issues:
    for issue in issues:
        if 'pull_request' not in issue:
            new_issue = create_issue(PUBLIC_REPO, issue)
            if new_issue:
                print(f"Issue {issue['title']} copied to {PUBLIC_REPO} as #{new_issue['number']}")
        else:
            new_pr = create_pull_request(PUBLIC_REPO, issue)
            if new_pr:
                print(f"Pull Request {issue['title']} copied to {PUBLIC_REPO} as #{new_pr['number']}")
