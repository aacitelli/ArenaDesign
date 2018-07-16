# ArenaDesign
### What is ArenaDesign?

ArenaDesign is a JavaFX program that I made mid-summer that displays a grid that users can interact with 
(similar to a system like Paint) to design their own laser tag arena.

### Current State of the Project

Currently my main project, and being actively worked on every day.

### What's next for the project?

##### High Priority

- Make line endpoints latch onto the nearest intersection (this will make it look amazing) - Lots of work has been done with this, but I'm almost finished
- Make it possible to delete lines (probably by right-clicking on them, that seems the most intuitive)
- Add the ability to add other structures instead of just walls (Bases, Energizers, Trussle, Pillars, and that's about it)

##### Relatively Low Priority

- Make it possible to have different shaped arenas instead of just a square (this shouldn't be insanely difficult, but it's obviously not easy
- Make a "preview line" pop up when the user is dragging the cursor around (this is difficult due to me using JavaFX and GraphicsContext being tricky in that regard, but I'll figure it out given time)

### What I've learned so far

This project was a great way to learn JavaFX, which is (through cursory research) one of the more modern ways to make GUI applications
in Java. Once I got past the initial learning curve, it was mostly just a matter of me looking up methods I needed and quite a bit of 
troubleshooting.

### Challenges

Due to the way I designed the program, it essentially automatically resizes to fit the content that is on the grid. This would
normally be good, but made it so I had to express most stuff in terms of screen width and other related things, instead of 
hardcoding values (which is a bad practice in general, and I'm glad that wasn't a viable outcome). 

I'm partially through collision (latching onto nearby intersections), and it's a pain, but doable. The main difficulty is keeping organized in terms of "pixels", and "grid units", because grid units are different depending on the dimensions you give the grid. I've ended up making conversion methods to make it easier to read, but it's still not a perfect product yet. It's getting there, though.

### Thoughts on Java/JavaFX

This is really the first GUI system I've used (except if you count Unity, which I haven't really gotten too in-depth with). However, I 
do like it. Everything was pretty straightforward, and everything fell into place pretty logically, and I didn't have to make too many
logical leaps to fix my issues. Overall, I'm a fan, but even though there are CSS options to fix this, it seems largely outdated. I plan
to learn Python's GUI system (Tkinter gets thrown around a lot) in the future, because it seems cool.




