---
title: "Setting up mu4e with iCloud custom domains with Doom Emacs on Arch Linux"
author: ["Luciano Laratelli"]
date: 2022-04-18T20:32:00-04:00
draft: false
---

Here's how I set up `mu4e` with Doom Emacs on Arch Linux for my custom domain
hosted on iCloud. I'm using `mbsync`, `mu`, and `msmpt`. I originally went with
a systemd timer as detailed in the first two sections as recommended in the [Arch
wiki](https://wiki.archlinux.org/title/isync#Calling_mbsync_automatically), but `mu4e` actually has a built-in functionality to deal with this for us.
Note that what I've written here is the _minimum_ I needed to do to actually
sync, send, and read emails from `emacs`. There's a lot more you can do.


## Configuration files

In `init.el`, under `:email`

```lisp
(mu4e +org)
```

`config.el`:

```lisp
(set-email-account! "icloud"
  '((mu4e-sent-folder       . "/icloud/Sent")
    (mu4e-drafts-folder     . "/icloud/Drafts")
    (mu4e-trash-folder      . "/icloud/Trash")
    (mu4e-refile-folder     . "/icloud/Archive")
    (smtpmail-smtp-user     . "lucianolaratelli")
    (mu4e-compose-signature . "\n\nLuciano"))
  t)

(after! mu4e
  (setq sendmail-program (executable-find "msmtp")
        send-mail-function #'smtpmail-send-it
        smtpmail-stream-type 'starttls
        message-sendmail-f-is-evil t
        message-sendmail-extra-arguments '("--read-envelope-from")
        message-send-mail-function #'message-send-mail-with-sendmail))
```

In `$HOME/.config/system/user/mbsync.timer`:

```text
[Unit]
Description=Mailbox synchronization timer

[Timer]
OnBootSec=1m
OnUnitActiveSec=5m
Unit=mbsync.service

[Install]
WantedBy=timers.target
```

`$HOME/.config/system/user/mbsync.service`:

```text
[Unit]
Description=Mailbox synchronization service

[Service]
Type=oneshot
ExecStart=/usr/bin/mbsync --verbose --all

[Install]
WantedBy=default.target
```

`$HOME/.mbsyncrc`:

```text
# imap account information
IMAPAccount icloud
Host imap.mail.me.com
User lucianolaratelli@icloud.com
PassCmd "secret-tool lookup email luciano@laratel.li"
SSLType IMAPS
Port 993

# remote storage (use the imap account specified above)
IMAPStore icloud-remote
Account icloud

# local storage
MaildirStore icloud-local
Path ~/Dropbox/mailbox/icloud/
Inbox ~/Dropbox/mailbox/icloud*Inbox
Subfolders Verbatim

# channel to remote storage
Channel icloud
Far :icloud-remote:
Near :icloud-local:
Patterns *
Create Near
Sync All
Expunge Both
SyncState *
```

The crucial part when you're using a custom domain hosted on iCloud is to use
your **iCloud email address** instead of the custom one. I thought this was a bug
with custom domains (I've run into another one) but I called Apple's support and
they told me I needed to use the iCloud email address. You can find this on an
iPhone or iPad by going to Settings, tapping on your name up top, and tapping on
`Name, Phone Numbers, Email`. I had `@me.com` and `@icloud.com` emails there. I
went with the `@icloud.com` one. [Apple's docs](https://support.apple.com/en-us/HT202304) on third-party iCloud clients say
you can use just the part before the domain, but I included the whole thing just
in case. Without further ado, `$HOME/.msmptrc`:

```text
defaults
tls_trust_file /etc/ssl/certs/ca-certificates.crt
logfile ~/.maildir/msmtp.log
protocol smtp

account icloud
auth on
host smtp.mail.me.com
port 587
protocol smtp
from luciano@laratel.li
user ${ICLOUD_EMAIL_ADDRESS}
passwordeval "secret-tool lookup email luciano@laratel.li"
tls on
tls_starttls on

account default : icloud
```

I have `tls` and `tls_starttls` both on. I think I only need one of these, but I
don't want to muck with testing my mail configuration to get a blog post out.
You might need one, the other, or both. Exercise for the reader!

I was originally using `gpg` as described by Erich Grunewald in his very helpful
[post](https://www.erichgrunewald.com/posts/setting-up-gmail-in-doom-emacs-using-mbsync-and-mu4e/#(optionally)-store-your-password-in-an-encrypted-file), but unlocking my yubikey every five minutes became a pain and I figured my
login keychain was secure enough for my (unsophisticated) threat model.


## Install and enable packages

```bash
yay mbsync
sudo pacman -S msmtp
yay mu mu4e # impossible to find mu otherwise

mkdir -p ~/home/Dropbox/mailbox/icloud
mbsync -Va
mu init -m ~/Dropbox/mailbox --my-address luciano@laratel.li
mu index

systemctl enable --user --now mbsync
systemctl enable --user --now mbsync.timer

doom sync
```

At this point you can run `emacs`, `<SPC> o m`, and get to emailin'!


## Moving away from systemd

This was working fine but I wasn't getting in-`emacs` notifications when new
emails came in, even though `mbsync` was running on schedule! So I got rid of
the `mbsync.timer` service with `systemctl disable --now --user mbsync.timer`. I
kept `mbsync.service` so that my email syncs when I log in for the day. Then, in
my `config.el`:

```lisp
(after! mu4e (setq mu4e-get-mail-command "mbsync --verbose --all"
                   mu4e-update-interval 300))
```

I restarted `emacs` and I was good to go.


## Resources

-   Tecosaur's awe-inspiring [config](https://tecosaur.github.io/emacs-config/config.html#fetching-systemd)
-   The already-mentioned [post](https://www.erichgrunewald.com/posts/setting-up-gmail-in-doom-emacs-using-mbsync-and-mu4e/#(optionally)-store-your-password-in-an-encrypted-file) from Erich Grunewald
-   The Doom Emacs `mu4e` module [documentation](https://github.com/hlissner/doom-emacs/tree/develop/modules/email/mu4e) (also from Tecosaur)
-   [davemail](https://github.com/kzar/davemail/blob/main/.mbsyncrc)
-   This [article](https://macowners.club/posts/email-emacs-mu4e-macos/#storing-trusted-root-certificates), though it focuses on macOS
