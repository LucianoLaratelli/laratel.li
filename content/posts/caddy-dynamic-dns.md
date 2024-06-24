---
title: "Setting up dynamic DNS for Namecheap using Dockerized Caddy"
author: ["Luciano Laratelli"]
draft: false
date: 2024-06-24
description: I compiled Caddy with custom modules using Docker to manage Dynamic DNS for namecheap
---

The internet is unusable without ad blocking, so I route all of our network
traffic through [Pi-hole](https://pi-hole.net/) running on my home server to
block ads. I want that same experience when I'm away from home, so I set up a
VPN with [WireGuard](https://www.wireguard.com/) to allow me to use my home
Pi-hole as the DNS server for my phone and laptop when on the go.

This works great except for when my home network falls over, or I lose power, or
when the unexpected happens. We recently went on vacation and, of course, the
day we got there, some "unexpected" happened and I was suddenly without an ad
blocker on my phone. Before figuring out what went wrong, I quickly spun up a
similar WireGuard + Pi-hole setup on a Droplet so I could use the internet
without losing my mind.

Thanks to a friend who stopped by our apartment for an unrelated reason, I
learned that the issue was that our ISP had changed our public IP on us. This is
the first time this has happened to me in the.... five? six? years I've been running
a homelab, over four different ISPs.

I thought [dynamic DNS](https://wiki.archlinux.org/title/Dynamic_DNS) would save
me, but it seemed clear that I'd configured `ddclient` incorrectly. Since I run
Caddy, I wondered if I could shove dynamic DNS into that. It turns out that's
possible with [this random "Caddy
app"](https://github.com/mholt/caddy-dynamicdns)I found, as long as I install
the right [Caddy DNS provider](https://github.com/caddy-dns). I use namecheap,
so I grabbed that one.

Using this Caddy app and a Caddy DNS provider involes compiling Caddy with these
modules. My home server uses Docker Compose (on Arch, btw) so I need to make an
image with the DNS provider built in. The
[documentation](https://hub.docker.com/_/caddy) has an example for this (under
`adding custom Caddy modules`), which is great!

My Caddy block previously looked like this:
```yaml
x-common-variables: &common-variables
  PGID: 1000
  PUID: 1000
  TZ: America/New_York

services:
  caddy:
    container_name: caddy
    image: caddy:2.8.4
    restart: unless-stopped
    environment:
      <<: *common-variables
      HOST: "my.host" # an actual domain name here
      LOCAL_IP: 192.168.1.2
    ports:
      - "80:80"
      - "443:443"
      - "443:443/udp"
    volumes:
      - ./appdata/caddy/Caddyfile:/etc/caddy/Caddyfile
      - ./appdata/caddy/site:/srv
      - ./appdata/caddy/data:/data
      - ./appdata/caddy/config:/config
```

Looking at the
[documentation](https://docs.docker.com/compose/compose-file/build/#illustrative-example) for Docker Compose, I want something like this:
```yaml
x-common-variables: &common-variables
  PGID: 1000
  PUID: 1000
  TZ: America/New_York

services:
  caddy:
    container_name: caddy
    restart: unless-stopped
    build: ./custom-caddy-build
    environment:
      <<: *common-variables
      HOST: "my.host" # an actual domain name here
      LOCAL_IP: 192.168.1.2
      NAMECHEAP_SECRET_KEY: "definitely-my-secret-key"
    ports:
      - "80:80"
      - "443:443"
      - "443:443/udp"
    volumes:
      - ./appdata/caddy/Caddyfile:/etc/caddy/Caddyfile
      - ./appdata/caddy/site:/srv
      - ./appdata/caddy/data:/data
      - ./appdata/caddy/config:/config
```

Then, in `custom-caddy-build/Dockerfile`:
```Dockerfile
FROM caddy:2.8.4-builder AS builder

RUN xcaddy build\
    --with github.com/caddy-dns/namecheap\
    --with github.com/mholt/caddy-dynamicdns

FROM caddy:2.8.4

COPY --from=builder /usr/bin/caddy /usr/bin/caddy
```

Then I ran `sudo docker-compose up -d` as normal and... it just worked. Neat!

Now I configure the dynamic DNS plugin, in my Caddyfile:
```
{
	dynamic_dns {
		provider namecheap {
			api_key {$NAMECHEAP_SECRET_KEY}
			user my-namecheap-user
		}
		domains {
			my.host @ www
			my.host my-subdomain
			my.host my-other-subdomain
		}
	}
}
```

I had to enable access to the [namecheap
API](https://www.namecheap.com/support/api/intro/) for my account before this
would work. `user` is just my username for namecheap. To use the API, I had to
add an IP address to their allowlist. This is concerning because if my IP
address changes (the exact problem I'm trying to solve), I'm not sure that I'll
be able to... actually hit the namecheap API to update my dynamic DNS? We'll
cross that bridge when we inevitably get to it.

Anyways, how do I check that everything worked? I set up an [A+ Dynamic DNS
record](https://www.namecheap.com/support/knowledgebase/article.aspx/43/11/how-do-i-set-up-a-host-for-dynamic-dns/)
pointing to 127.0.0.1. I figured if that value gets updated when I restart
Caddy, that means I've set things up correctly. That... did not work. The A+
record would (inexplicably) get changed to an A record with the same placeholder
IP address.

My next validation attempt was to change the IP address of a subdomain. I
observed a change here! The list of subdomains got a new entry for the subdomain
I changed with the correct IP address, with the old placeholder one still
present as well. This is [apparently a known problem that is namecheap's
fault](https://github.com/mholt/caddy-dynamicdns/issues/49) so I won't sweat it
too much, because the DNS does still work.

The dynamic DNS thing (a "Caddy app") has a `dynamic_domains`
[feature](https://github.com/mholt/caddy-dynamicdns?tab=readme-ov-file#sample-configurations)
that I used originally, but it spammed a bunch of messages in my logs when I
tried to use it:
```
caddy  | {"level":"info","ts":1719245958.4446826,"logger":"dynamic_dns","msg":"domain not found in DNS","domain":"atuin.my.host"}
```

I ended up specifying my domains manually, as above. This does turn into One
More Thing I Have To Remember To Do Whenever I Add A New Service To My Home Lab but that's okay. 

Last thing is to format my Caddyfile: 
```
sudo docker exec -it caddy caddy fmt --overwrite /etc/caddy/Caddyfile
```

Neat!
