# JSONflatten
## Flattens nested json into key value pairs.

### How to run ?

```
git clone git@github.com:srikalyc/JSONflatten.git
cd JSONflatten
mvn clean install -DskipTests=true
java -jar target/JSONflatten-1.0.jar path_to_json_file
```

### Sample input and output

**Sample input** 

```
 {
  "a" : 1,
  "b" : ["one", "two"],
  "c" : [{"a":1}, {"b" :2}]
 }
```
 
**Sample output**
 
```
 /a->1
 /b/0->one
 /b/1->two
 /c/0/a->1
 /c/1/b->2
```

### Use cases
1. Embed in shell scripts and use grep etc to extract very specific key values burried deep in JSON.
2. XPath like way to extract json key values. 
