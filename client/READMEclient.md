# Setup

## Proxy

Proxy is used to allow frontend to communicate with backend without using CORS in cross origin connection.
A proxy-config.js file is created for connection to make it a same origin connection.
To serve the angular page for development
**Use the following command:**
```
ng serve --proxy-config proxy-config.js
```
For deployment:
```
ng build
```
Copy the files in the dist folder of angular into the static folder of springboot