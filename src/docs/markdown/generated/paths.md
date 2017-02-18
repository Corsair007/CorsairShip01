## Paths
### 根据用户id查询用户信息
```
GET /user/queryUserById{?userId}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|QueryParameter|userId|用户Id|true|integer (int32)||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|OK|Result|
|500|500 message|Error|


#### Consumes

* application/json

#### Produces

* application/json

#### Tags

* user-controller

