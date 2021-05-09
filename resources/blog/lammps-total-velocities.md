---
title: "Calculating System Velocities Using LAMMPS"
date: 2018-07-18T20:09:11-04:00
---

For the water research I've been doing, it is useful to get the velocity autocorrelation function, which gives the density of states of the system. Here is how I ended up doing it:

```lammps
compute 1 all property/atom vx vy vz

compute s_v_x all reduce sum vx
variable vel_x equal c_s_v_x
fix print_vel_x all print 1 "$(step) ${vel_x}" file "vel_x.txt" screen no

compute s_v_y all reduce sum vy
variable vel_y equal c_s_v_y
fix print_vel_y all print 1 "$(step) ${vel_y}" file "vel_y.tyt" screen no

compute s_v_z all reduce sum vz
variable vel_z equal c_s_v_z
fix print_vel_z all print 1 "$(step) ${vel_z}" file "vel_z.tzt" screen no

```

One of the things that always trips me up in LAMMPS is the difference between using variables created by the user (e.g. `vel_x` above) and using variables LAMMPS provides (e.g. `step`.) When using them in, for example, a `fix print`, user-created variables must be wrapped in curly brackets, while the LAMMPS-provided variables must be wrapped in parens.
