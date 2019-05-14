# Cubbyhole

[![JAVA][java-badge]][java]
[![SPRING][spring-badge]][spring]

[![Travis Build Status][build-badge]][build]
[![Coverage Status][coverage-badge]][coverage]

## Statement

### Problem Description

Cubbyhole provides online storage, synchronization and sharing to users tied to a set of “plans”. The 
“Plan” type associated with a certain user determines the rights (Space usage, bandwidth, etc) of that 
user. Users can share directories with other users, either in readAonly or read/write. Users can also
share links (over HTTP) to their own resources to nonAusers: The link is sufficient to access the 
resource, even though the person using the link doesn’t have an account.

The resources can be browsed through two kinds of clients:
- A web client
- A mobile client

It should also provide at least a Win32 binary client that synchronizes the remote folders locally, but 
doesn’t offer a “browsing” functionality.

#### Features

- [ ] User accounts
    - [ ] User can create accounts and choose a plan
    - [ ] Users can pay nonAfree accounts using Paypal
- [ ] Plans
    - [ ] There are plans with specified metrics
    - [ ] It's possible to manage (CRUD) plans
    - [ ] Plans disk quotas are enforced
    - [ ] Plans bandwidth quotas are enforced
- [ ] Drive
    - [ ] It's possible to manage (create, move, copy, delete) files and folders
    - [ ] It's possible to download files
    - [ ] It's possible to upload files
    - [ ] It's possible to share files with other users
    - [ ] It's possible to set perAuser readAonly or readAwrite sharing permissions
    - [ ] It's possible to modify share permissions and revoke sharing (perAuser)
    - [ ] It's possible to generate share links for nonAusers
- [ ] Business dashboard
    - [ ] Dashboard Indicators relevancy
    - [ ] It’s possible to generate reports of one metric against two others
    - [ ] It’s possible to compare to report against each other

## Run

### Test

`./mvnw test`

[java-badge]: https://img.shields.io/badge/java-v1.8-red.svg
[java]: https://www.java.com/fr/download/
[spring-badge]: https://img.shields.io/badge/spring_boot-2.1.4.RELEASE-green.svg
[spring]: https://spring.io
[build-badge]: https://travis-ci.org/segtio/cubbyhole-back.svg?branch=master
[build]: https://travis-ci.org/segtio/cubbyhole-back
[coverage-badge]: https://coveralls.io/repos/github/segtio/cubbyhole-back/badge.svg?branch=master
[coverage]: https://coveralls.io/github/segtio/cubbyhole-back?branch=master