# PokemonGoBack
Introduction
The Pokémon Trading Card Game (TCG) is a collectible card game with a goal similar to a Pokémon battle in the video game series. Players use Pokémon cards, with individual strengths and weaknesses, in an attempt to defeat their opponent by "knocking out" his or her Pokémon cards. The Pokémon Trading Card Game is a two-player game for all ages. Each player builds a Deck of sixty (60) cards using a combination of various "Pokémon cards" (the main type of cards used to battle), "Trainer cards" (cards with special effects), and "Energy cards" (cards that are required to perform most "Attacks") [1]
The project is implementing of a ‘single-player mode’ game. It provides 58 different types of cards that they get declared due to parsing a text file once the game starts. The AI part plays as our opponent. The software enjoys a GUI that allows the user to have some graphical interactions during the game. 
Figure 1: Pokémon game area
An Overview of How Pokémon Works
The Pokémon installation comprises three parts:
1.	The executable file named ‘Pokemon.jar/exe’.
2.	The meta files, parsed by executable file once the software is launched. By default, these files are placed in ‘Asset’ folder in installation directory.
3.	The deck files. Each player has his/her own deck text file that contains a list of indexes of those cards that the player wants to have in his/her deck at the start point. These files should be placed into the ‘Asset’ folder as well.

A typical Pokémon game architecture might be like figure 2.
![test](https://github.com/h0111in/PokemonGoBack/Documents/game-screenshot.jpg)
Figure 2: Pokémon game architecture

*	Core / Logic unit controls rules of the game such as defining who goes first? Managing turns, setting up the game or identifying winner the match.
*	GUI, it interacts with user by receiving user commands and passing them to the logic and consequently refreshing the user interface based on models.
*	AI part, plays as an opponent and invokes commands to the logic unit.
*	Meta Data provides required information about cards, abilities and list of cards for each player.

The project focuses on obtaining rules of the game, not only those related to general turns, but also the correctness of the operation of each ability in cards.
Pokémon project is open source or free software (GPL). It is written in Java. This project was implemented as a course project for SOEN 6011 (Software Engineering Processes) at Concordia university.
Potential Improvements
1.	Since the project uses a built-in AI module within the logic unit, extracting AI responsibilities from logic controller might be a useful modification to increase cohesion and make low coupling.
2.	By default, the software logs players’ operations into a text file just for further monitoring. Using a database instead of text file to store the meta data and latest state of players makes software more stable during the game and efficiency issues as well.
