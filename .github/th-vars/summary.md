### ./.github/vars/var-aaa.json
```json
{
  "a": "the value of property 'a'",
  "b": "the value of property 'b'",
  "c": {
    "d": "the value of property 'c.d'",
    "e": "the value of property 'c.e'"
  },
  "seq" : ["1", "2", "3"]
}
```
### ./.github/vars/var-bbb.json
```json
[
  {
    "a": "the value of property '[0].a'",
    "b": "the value of property '[0].b'"
  },
  "the value of element '[1]'",
  "the value of element '[2]'",
  [
    "the value of element '[3][0]'",
    "the value of element '[3][1]'"
  ]
]
```
