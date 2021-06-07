
# Steps to run the application
- Register your application following [instruction](https://developers.miro.com/docs/getting-started)
- Once you have "Client ID", copy `application-local_template.yml` to `application-local.yml` and fill fields:
`clientId`, `clientSecret` and optional `teamId`.
- Run spring-boot application (see below instruction if you want to run it in IDEA)
```shell
mvn spring-boot:run -Dspring-boot.run.profiles=local
```
The service will start on localhost:3333
- Start ngrok to make your application available on HTTPS from the internet:
```shell
ngrok http --log stdout localhost:3333
```
or if you have paid subscription you can have fixed subdomain
```shell
ngrok http --subdomain my-test-miro-app --log stdout localhost:3333
```
- Check the ngrok log, you'll see the ngrok domain in it (HTTPS address):
```
t=2021-06-07T12:52:44+0300 lvl=info msg="started tunnel" obj=tunnels name=command_line addr=http://localhost:3333 url=https://9744fbc871a8.ngrok.io
```
* Open the link (e.g. https://9744fbc871a8.ngrok.io) and you'll see the plugin welcome page
* Copy "Redirect URI for OAuth2.0" value to your plugin settings page
* TODO set up Permissions
* Click Authorize button and check the installation flow
* If your `teamId` parameter is set, you will have "Installation management URL" filled (e.g. to revoke the token)

# Run in IDEA
Customize `MiroAppOAuthApplication` run configuration, you should execute Maven goal `generate-resources`
before starting spring-boot application. Also, please provide "Active profiles" value (usually it should be `local`).

# Application State page
To avoid confusion, please note, that "Session ID" and "User ID" on the "State" page are just values within your
browser session based on cookies (this way you can check different authorizations).

