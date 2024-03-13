---
title: "Setting up unattended access for our building's keypad"
autor: ["Luciano Laratelli"]
date: 2024-03-13
draft: false
description: I wired up our building's keypad to a twilio phone number (and accompanying Twilio Function) to set up unattended access for emergencies.
---
Our building has this keypad for opening the front door:

{% image "./the-keypad.jpeg", "Picture of the keybad at our front door. It has a black receiver on the top left, a small screen just to the right of that, and a keypad on the bottom right. The whole keypad is made out of some stainless-steel looking metal." %}

This keypad is half of the entry system. It allows guests to enter a code for
the unit they want to access. The computer that's hooked up to the keypad is
also connected to a phone line. When it receives a code, it maps that to a phone
number (provided manually at move-in time) and rings the designated person, who
then enters `9` on their phone to grant access. The other half of the entry
system is for owners and residents. We use a
[fob](https://web.archive.org/web/20230319133108/https://cdn.shopify.com/s/files/1/0173/0271/6516/products/TRANSPROXLinear4button_1512x.jpg?v=1586052529)
to get access via doors as well as open the gate to the building's parking
garage. This system works well enough for most cases, but I had some items on my
wish list:

 - The system lacks *emergency access*. If I were to lose my fob, I would have
   no way to get into the building on my own; I would have to either phone a
   neighbor, bother someone on the HOA board, or wait for someone to walk in or
   out.
 - Giving out the code that routes to my unit is tantamount to giving out my
   phone number. After some time at our last apartment complex, I started
   receiving calls from the gate even when I wasn't expecting any visitors or
   deliveries. Besides being frustrating, this is also a security concern: if
   I'm expecting a visitor and someone tries my code around the time my visitor
   should arrive, I may mistakenly allow the wrong person in.[^1]
 - The system lacks *unattended access for trusted guests*. I can't always pick
   up the phone, even if I am home.

Two other motivating factors are that fobs cost $35 each and there's a hard cap
on the quantity of fobs the building will issue us.[^2] 

I volunteer for the building by setting up the fobs and phone numbers for new
residents, which has had two main benefits. The first is that I get to meet new
folks as they come to our building, which is always nice. The second is that I
(necessarily) have access to the awful, Windows 7-running Dell PC that controls the
fob system, which means I can mess around with the values for *our* unit
whenever I want.

Here's what I did: I added a second code for our unit that calls a Twilio
number. That Twilio number is using a [Twilio
Flow](https://www.twilio.com/docs/studio/user-guide/get-started), a no-code
thing, to accept input from the person at the door. It validates if that input
is a code I've provided, then sends the Door Open signal (the number 9) back to
the keypad. The flow looks like this:

{% image "./twilio-flow.png",  "This is a screenshot of a Twilio Flow. The flow is essentially a state machine diagram. It starts with a Trigger, which transitions to a block that says 'hello!' to the User on the 'incoming call' trigger. After that greeting, we move to a block called 'Init', which asks the user to enter their code, followed by the pound key. If the user says anything or there is no input, we loop back to Init. If the user entered keys, we validate the code by sending it to a URL. If that URL returns success, we have our result: the digit 9. If not, we say 'Code is invalid.'" %}

That redacted URL points at a [Twilio Function](https://www.twilio.com/docs/serverless/functions-assets/functions) (classic[^3]) which is not much code at all:
```javascript
exports.handler = async (context, event, callback) => {
  const good = new Set([
    '123456', // definitely my real code
    '234567', // real code for my neighbor
    '345678', // absolutely the real code for a trusted friend
  ]);
  
  if (good.has(event.code)) {
    return callback();
  } else {
    return callback('bad code');
  }
}
```

This has been a really gratifying real-world (i.e., not just software) project
for me. It has saved me *multiple* times that I've left my fob at home by
accident. I forget it's there until I need it and it has not failed me yet. This
setup combined with a lockbox with a key to our front door at an undisclosed
location makes it so a lot more has to go wrong for us to be locked out of our
apartment. I've paid Twilio $41.36[^4] since February 12th, 2023 (when I set this
up) which amounts to 10.47 cents a day[^5]. It is well worth it, both for the peace of
mind and the *delight* I get when I use it.

When I started this project, I had this whole vision for some web-admin
interface to add codes dynamically for deliveries, be able to only allow them on
certain times, etc. In a rare (and maybe my first ever) moment of Technical
Maturity, I said no to that and kept it as simple as you see here.


[^1]: These systems ostensibly allow for communication between the person at
the gate and the unit owner, but their common exposure to the elements makes
this not work well in practice. At two of the three complexes we've lived in
with these systems, it was impossible to hear what the person at the gate was
saying.


[^2]: This is as per the HOA rules. I know for a fact this doesn't get followed
but I'm not a cop.

[^3]: At some point before I started this, they had already come out with New
    Functions and suggested not using Functions (Classic) but the classic one
    seemed simpler to me at the time.


[^4]: I've paid them $20, $10.27, and $11.09. I have "auto-recharge" turned on, so when my balance on twilio dips below $10 I recharge up to $20. If we ever move I'll have to remember to disable the auto-recharge and then use it a bunch to not leave that money on the table.

[^5]: Computed 2024-03-13

