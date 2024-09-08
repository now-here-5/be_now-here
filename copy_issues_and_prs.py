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
    return response.json()

def create_issue(repo, issue):
    url = f'https://api.github.com/repos/{repo}/issues'
    data = {
        'title': issue['title'],
        'body': issue['body'],
        'labels': [label['name'] for label in issue.get('labels', [])],
        'state': issue['state']
    }
    response = requests.post(url, json=data, headers=headers)
    return response.json()

def create_pull_request(repo, issue):
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

issues = get_issues_and_prs(PRIVATE_REPO)
for issue in issues:
    if 'pull_request' not in issue:
        new_issue = create_issue(PUBLIC_REPO, issue)
        print(f"Issue {issue['title']} copied to {PUBLIC_REPO} as #{new_issue['number']}")
    else:
        new_pr = create_pull_request(PUBLIC_REPO, issue)
        print(f"Pull Request {issue['title']} copied to {PUBLIC_REPO} as #{new_pr['number']}")
