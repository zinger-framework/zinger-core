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
  <a href="LICENSE"><img alt="License" src="https://img.shields.io/github/license/zinger-framework/zinger-core"></a>
  <a href="https://zinger.pw/docs/api"><img alt="Documentation" src="https://img.shields.io/badge/code-documented-brightgreen.svg?style=flat-square"></a>
  <a href="https://github.com/zinger-framework/zinger-rails/pulls"><img alt="PRs Welcome" src="https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square"></a>
</p>

An Open Source Framework for building Ecommerce marketplace solutions, offering highly flexible backend system that enables the developers to manage shops, inventory, orders and discounts along with authentication, order fulfilment, webhook notifications and various payment integrations.

## Architecture
<img src="https://raw.githubusercontent.com/ddlogesh/zinger-framework-docs/master/website/static/img/zinger/zinger-v2-arch.png" height=75% width=75%/>

#### Zinger Core
&emsp;&emsp;A backend server that exposes the REST API, written in Ruby on Rails using PostgreSQL database and Redis cache. It also exclusively uses Elastic Search for searching inventory, shops, etc.

#### [Zinger Client](https://github.com/zinger-framework/zinger-ui)
&emsp;&emsp;Zinger Client is a frontend application built in Angular, incorporating Zinger Admin and Zinger Platform for managing admin and platform specific operations respectively.

#### Zinger Admin
&emsp;&emsp;A web dashboard providing a one-stop solution for shop employees to manage and track orders from their clients and it provides an effective order and inventory management solutions.

#### Zinger Platform
&emsp;&emsp;A web dashboard to monitor the zinger customer and admin applications. It is a powerful tool to track application performance, manage outlet information and promotes new arrivals to increase brand engagement.

#### [Zinger Customer](https://github.com/shrikanth7698/Zinger-Android-App)
&emsp;&emsp;A customer-side mobile application to place and track your orders from multiple store partners near your location. It allows you to browse catalogs of all stores and provides you a seamless shopping experience from order acceptance to last mile delivery.

## Database Schema
Check out the [database schema](https://drawsql.app/zinger-technologies/diagrams/zinger-v2)

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
- Navigate to project root directory & run `docker-compose up`
- The angular application will be running in the following endpoints:
    - Customer APIs - http://api.zinger.pw
    - Admin APIs - http://admin.zinger.pw
    - Platform APIs - http://platform.zinger.pw

#### Deploy

If you would like to integrate with any mobile or web application, use [Ngrok](https://ngrok.com/download) to get a public URL of
your localhost. Also, you can host in any of the online cloud service providers like [Heruko](https://www.heroku.com/), [Azure](https://azure.microsoft.com/), [AWS](https://aws.amazon.com/), etc.

## Community

Zinger framework is completely free and made open-source. Our team is really happy to support contributors from all around the world. Fork our project and send us your pull request: maybe sample storefronts using our framework, minor extensions or major improvements.

**Every contribution is awesome and welcome!**
