```toml
name = 'POST add invoice'
method = 'POST'
url = '{{url-radiant}}/invoice/test/create'
sortWeight = 1000000
id = '9c3b730c-c862-4710-99ad-89dceb102741'

[[headers]]
key = 'Authorization'
value = 'Bearer Token eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGUiOiJBRE1JTiIsIm5hbWUiOiJBZG1pbiIsImlhdCI6MTc0MjQ5Mjc5MywiZXhwIjoxNzQyNTc5MTkzfQ.SQLZ2-vqRBckV1T3eKMY5_KL9ilxf2EBPMTClN_uovQ'

[auth.bearer]
token = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGUiOiJBRE1JTiIsIm5hbWUiOiJBZG1pbiIsImlhdCI6MTc0MjQ5Mjc5MywiZXhwIjoxNzQyNTc5MTkzfQ.SQLZ2-vqRBckV1T3eKMY5_KL9ilxf2EBPMTClN_uovQ'

[body]
type = 'JSON'
raw = '''
{
  "purchaseOrderId": 52,
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
