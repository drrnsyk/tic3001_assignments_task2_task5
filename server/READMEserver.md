# Setup

## Marvel API
Requires a public key which is also the api key and a private key.
Params required for the api query string: ts - long string timestamp, public key and a hash (md5(ts+privateKey+publicKey)).
**Set enviroment variables for public key and private key**.
Commands for setting envrioment variable locally:
```
set PUBLIC_KEY=<publicKey>
set PRIVATE_KEY=<publicKey>
```
For deployment, add it into the enviroment variables

## REDIS
Set up application properties with the host, port, database, username.
**Use the following command to set up the REDIS password as an eviroment variable**
Commands for setting up REDIS password in enviroment variable locally:
```
set REDIS_PASSWORD=<redis password>
```
For deployment, add it into the enviroment variables
