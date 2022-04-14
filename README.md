<h1 align="center">
  Zinger Framework
</h1>
<h4 align="center">
  Build your own Ecommerce Marketplace 🛍️
</h4>

<p align="center">
  <strong>
    <a href="https://zinger.pw">Website</a>
    |
    <a href="https://zinger-workspace.slack.com/join/shared_invite/zt-e6xt0gc2-nBEy85RhEy7NZv3gWCt6Dg/">Slack</a>
    |
    <a href="https://discord.gg/TqADaXV">Discord</a>
    |
    <a href="https://stackoverflow.com/questions/tagged/zinger">StackOverflow</a>
  </strong>
</p>

<p align="center">
  <a href="LICENSE"><img alt="License" src="https://img.shields.io/github/license/zinger-framework/zinger-rails"></a>
  <a href="https://zinger.pw/docs/api"><img alt="Documentation" src="https://img.shields.io/badge/code-documented-brightgreen.svg?style=flat-square"></a>
  <a href="https://github.com/zinger-framework/zinger-rails/pulls"><img alt="PRs Welcome" src="https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square"></a>
</p>

An Open Source Framework for building Ecommerce marketplace solutions, offering highly flexible backend system that enables the developers to manage shops, inventory, orders and discounts along with authentication, order fulfilment, webhook notifications and various payment integrations.

## Tech Stacks
* Rails **6**
* PostgreSQL **12.1**
* Redis

## Prerequisites
#### Install Applications
* Docker Desktop

#### Update Hosts
Add the below lines to your hosts file to run the application in custom subdomain.
```shell
127.0.0.1	api.zinger.pw
127.0.0.1	admin.zinger.pw 
127.0.0.1	platform.zinger.pw
```
Host file location
- Linux/Mac - `/etc/hosts`
- Windows - `C:\Windows\System32\drivers\etc\hosts`

## Setup instructions
#### Fork Project
- Fork the main repository
- Clone the forked repository locally `git clone forked_repo_url`

#### Run Project
- Navigate to root directory & run `docker-compose up`
- The angular application will be running in the following endpoints:
    - Customer APIs - http://api.zinger.pw
    - Admin APIs - http://admin.zinger.pw
    - Platform APIs - http://platform.zinger.pw

## Community

Zinger framework is completely free and made open-source. Our team is really happy to support contributors from all around the world. Fork our project and send us your pull request: maybe sample mobile apps using our framework, minor extensions or major improvements.

**Every contribution is awesome and welcome!**
