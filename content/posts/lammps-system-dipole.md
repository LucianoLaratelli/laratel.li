---
title: "Calculating System Dipoles Using LAMMPS"
date: 2018-07-10T11:23:05-04:00
---

As part of my research in Philadelphia with Matt, we needed to calculate system dipoles using LAMMPS. We were having a lot of trouble using the provided `dipole/chunk` compute so we went about figuring it out another way instead. This was a pain to do (could not find it anywhere on the internet, probably because everyone else is smart enough to figure out how to use `dipole/chunk`) so here is how we ended up doing it:

```text
variable dipole_x atom x*q
variable dipole_y atom y*q
variable dipole_z atom z*q
#systemDipole_x
compute sD_x all reduce sum v_dipole_x
compute sD_y all reduce sum v_dipole_y
compute sD_z all reduce sum v_dipole_z

#just in case you need only one dipole...
#variable system_dipole_x equal c_sD_x
variable total_system_dipole equal sqrt((c_sD_x*c_sD_x)+(c_sD_y*c_sD_y)+(c_sD_z*c_sD_z))

fix get_total_system_dipole all print 1 "$(step) ${total_system_dipole}" file "dipole.txt"
```
