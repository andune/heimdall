Sonar
=====

I use Sonar as a code health management tool and generally expect pull requests to either improve code quality or at least not diminish it any. Here is the link to the [Sonar dashboard](http://www.andune.com:9000/dashboard/index/1) for this project.

You can find and download my Sonar [Quality Profile](http://www.andune.com:9000/rules_configuration/index/5) for your own use.

This is the default 'Sonar way with Findbugs' profile with some minor changes:

* I don't enforce if/else braces for single-line statements
* I allow 'static final Logger log', whereas the default requires all static final be all caps.
* Cyclomatic method complexity is set to 15, instead of default 10. I find 15 to be reasonable.

You can download this profile and use it in your own Sonar installation for this project. I will check your pull requests with Sonar prior to merging and if the project RCI goes down instead of up from your pull request, I may ask you to fix your code to be compliant.

In general, Sonar is a fantastic automated way to enforce code quality. But you may not be familiar with Sonar or want to wait for Sonar to run to check every change you might want to make, so below is some additional documentation of the code standards I follow on my projects.

However, there are some rules enforced by the default Sonar policies that conflict with the below coding standards. For example, the Oracle coding standards allow trailing // comments on a line following code, Sonar has a rule that these be on a separate line. Where the two conflict, I prefer the Sonar rules; in general it produces cleaner code, but it is also very easy to objectively enforce since the code checks are automated.


General Guidelines
==================

In general, you should follow the [Oracle Java coding standards](http://www.oracle.com/technetwork/java/javase/documentation/codeconvtoc-136057.html). If you follow these standards, for the most part your code will pass Sonar checkstyle with minimal rules violations.  Where there might be minor rules violations (such as // comments on lines with code), they are minor enough for me to fix if you don't happen to have Sonar handy to check yourself.

However, I do have a few personal style differences from the Oracle Java standards, as noted here:

* I do not require braces {} for single-statement if/else. I don't mind them being used, so if that's your personal preference for code you contribute, that's fine. But don't reformat the entire code base according to this standard and submit a PR, I won't pull it.

* I do not like else statements on the same line as ending braces, as in:

  ```java
  if {
   // do something
  } else {
   // something else
  }
  ```

  If this is your personal preference or your IDE is setup for new code to be formatted this way, it's a minor thing and I will probably accept your pull request anyway and just auto-reformat it to my style preference.

* I don't care about line length. I generally try to adhere to the 80-line limit when I can; however, often time breaking up code arbitrarily to enforce this limit results in ugly code. I prefer easily readable code over arbitrary 80-character line break enforcement.

License
=======
All code submissions must be licensed under GNU General Public License v3. By submitting your code, you are agreeing to release your code under this license.
