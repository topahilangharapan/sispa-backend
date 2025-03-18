```toml
name = 'POST login'
method = 'POST'
url = '{{url-radiant}}/auth/login'
sortWeight = 2000000
id = '1557e435-91d6-4e3d-af6d-b2c338920b1b'

[[queryParams]]
key = 'username'
value = 'admin'
disabled = true

[[queryParams]]
key = 'password'
value = 'admin'
disabled = true

[body]
type = 'JSON'
raw = '''
{
  username: "admin",
  password: "admin"
}'''
```
