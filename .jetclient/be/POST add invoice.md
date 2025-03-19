```toml
name = 'POST add invoice'
method = 'POST'
url = '{{url-radiant}}/invoice/create'
sortWeight = 1000000
id = '9c3b730c-c862-4710-99ad-89dceb102741'

[auth.bearer]
token = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsIm5hbWUiOiJBZG1pbiIsInJvbGUiOiJBRE1JTiIsImlhdCI6MTc0MjMxODYzNCwiZXhwIjoxNzQyNDA1MDM0fQ._NqsYxU-0xdEZha_OqUhmP57S_jcF6H93DTEV6oKYGk'

[body]
type = 'JSON'
raw = '''
{
  "purchaseOrderId": 54,
  "receiver": "Jamal Aksi Laga",
  "datePaid": "17 Maret 2012",
  "ppnPercentage": "100",
  "bankName": "Bank-e",
  "accountNumber": "49152877",
  "onBehalf": "Forum Pecinta Jamal",
  "placeSigned": "Aceh",
  "dateCreated": "17 Maret 2012",
  "dateSigned":"17 Maret 2012",
  "signee": "Ketua Forum Pecinta Jamal",
  "event": "THR"
}'''
```
