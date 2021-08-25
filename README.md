# Priconne Server Backend For Clanbattles
## Description
This is a REST templates backend written in Java Spring to help clans to organize and track clanbattles. I thought of writing a website to replace and unify the stacks of excel sheets and discord announcements.

### Current Features
* Implemented clanbattle, player, character as the main endpoints.
* Characters provide an easy way to interact with teams.
* Players are modeled after the in-game players and have a unowned character list to help in assigning recommended teams.
* Clanbattles have recommended team models that can be assigned to each different encounter, this can be checked by players to whether they'd be suitable to take on this challenge.
* Damages done during these encounters can be recorded and then be retrieved with respect to encounter or player.

### Could Be Implemented Features
* Spring Security can be used for authorizing only the clan members into the site. I suggest using another User entity for this.
* A blog-like front page could be used (however, I think discord is better for that)
* Character interface can include a item finder utility, should the website be truly customized to be a one-for-all.

## Initialization 
This project requires maven as well MySql to work properly. You'll need to add some character information after the initialization, the empty database templates will be created by hiberante. You can either assign the character ids yourself or use the ones provided by a data mine, the choice is yours.

## Contribution
* This is one of my beginner project, so please respectful when contributing. 
* Write clearly what has changed in your commit message.
* Don't commit directly to master.
