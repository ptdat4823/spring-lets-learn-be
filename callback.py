offset = 120 * 24 * 60 * 60

commit.author_date = (
    str(int(commit.author_date.split()[0]) + offset).encode() + b' ' + commit.author_date.split()[1]
)

commit.committer_date = (
    str(int(commit.committer_date.split()[0]) + offset).encode() + b' ' + commit.committer_date.split()[1]
)