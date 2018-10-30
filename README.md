# ArenaDesign
### What is ArenaDesign?

ArenaDesign is a JavaFX program that I made mid-summer that displays a grid that users can interact with 
(similar to a system like Paint) to design their own laser tag arena.

### Current State of the Project

Haven't worked on it in a while, but I'll probably pick it back up at some point. 

### What's next for the project?

##### High Priority

- Fix snapping errors - It never works first time, and it starts acting funny after a distance ends up being too long. Something probably wrong with the distance method, but I've looked through it several times and haven't found anything
- Add delete functionality (Dragging over lines w/ right click enabled seems the most intuitive, but that's kind of complicated to program due to how GraphicsContext is implemented in my program)(Duplicate this to the other side too)
- Add the ability to add other structures instead of just walls (Bases, Energizers, Trussle, Pillars, and that's about all I can think of)

##### Relatively Low Priority

- Make every barrier replicate itself across
- Make it possible to have different shaped arenas instead of just a square (this shouldn't be insanely difficult, but it's obviously not easy
- Make a "preview line" pop up when the user is dragging the cursor around (this is difficult due to me using JavaFX and GraphicsContext being tricky in that regard, but I'll figure it out given time)

### What I've learned so far

This project was a great way to learn JavaFX, which is (through cursory research) one of the more modern ways to make GUI applications
in Java. Once I got past the initial learning curve, it was mostly just a matter of me looking up methods I needed and quite a bit of 
troubleshooting.

I also had to adjust to converting between "grid units" and pixels, which, after I got organized, turned out not to be very hard, but I had to stay really organized so that I didn't lose myself in the code. 

### Challenges

Due to the way I designed the program, it essentially automatically resizes to fit the content that is on the grid. This would
normally be good, but made it so I had to express most stuff in terms of screen width and other related things, instead of 
hardcoding values (which is a bad practice in general, and I'm glad that wasn't a viable outcome). 

It took me a while, but I eventually did make snapping to the nearest point work. I had to troubleshoot several errors that all took me a lot of time to fix (several hours total, at least). It was a lot of fun, though, because the payoff when you finally fix it is worth it. The biggest issue essentially boiled down to (at its simplest level) using a variable's value before it was actually assigned, but that's a massive simplification. 

### Thoughts on Java/JavaFX

This is really the first GUI system I've used (except if you count Unity, which I haven't really gotten too in-depth with). However, I 
do like it. Everything was pretty straightforward, and everything fell into place pretty logically, and I didn't have to make too many
logical leaps to fix my issues. Overall, I'm a fan, but even though there are CSS options to fix this, it seems largely outdated. I plan
to learn Python's GUI system (Tkinter gets thrown around a lot) in the future, because it seems cool.






