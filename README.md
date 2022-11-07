# RailSwitch
Enhanced redstone features for detector rails, allowing players to build advanced routing systems.

## Usage
### Travelers
When traveling on RailSwitch rails, you need to set a destination. Enter the command `/dest`, followed by any number of words. Each word becomes one of your destinations. For example: `/dest icenia ita factory` sets 3 active destinations: `icenia`, `ita`, and `factory`. Using `/dest` by itself clears any destinations you had set previously.

Destinations do nothing on their own. It is up for rail engineers to build RailSwitches to handle these destinations.
### Rail engineers
RailSwitches modify the redstone output from detector rails, only allowing output when certain conditions are met. Note: this only applies to minecarts with players. Minecarts without players will trigger detector rails normally.

To build switches:
1. Place a sign above or next to a detector rail. **The sign must be reinforced on the same group as the detector rail.**
2. Put a RailSwitch on the first line. The simplest switch is `[destination]`.
3. Put switch details on the rest of the lines. For `[destination]` switches, each line is a destination to test for.
4. When a player passes over the detector rail, the switch will try and execute based on the player's set destinations. For `[destination]` switches, the detector rail outputs redstone if the player has any one of the listed destinations set.

For more advanced switches, it is possible to make errors. If a switch has an error in it, a puff of smoke will appear when a player tries to activate it.

## RailSwitches

### Simple Destination Check
- Normal: `[destination]` or `[dest]`
- Inverted: `[!destination]` or `[!dest]`

Outputs a redstone signal if the player has any one of the listed destinations currently set. Each detail line is considered 1 destination, meaning a single simple destination switch can check for up to 3 destinations. The exact destination `*` will match if the player has atleast 1 destination set. If the inverted variant is used, a redstone signal will be outputted only if the player has none of the listed destinations set.

Example: The following switch would output a redstone signal if the player has any one of `ti`, `temporal`, or `archive` destinations currently set.
```
[destination]
ti
temporal
archive
```

### RegularExpression Destination Check
- Normal : `[destex]` or `[destex;<flags>]`
- Inverted: `[!destex]` or `[!destex;<flags>]`

Outputs a redstone signal if any one of the player's destinations matches the given regular expression. All three detail lines are concatenated together (no separating characters) to form the regular expression. The [REJ2 regex library](https://github.com/google/re2j) performs the matching to prevent people from trolling the server with catastrophic backtracking. If the inverted variant is used, a redstone signal will be outputted only if none of the player's destinations match the regular expression. Optionally, one or more flags can be specified in the header to change the behavior of the matching.

Accepted regular expression flags:
- `i` - ignore case while matching destinations
- `m` - match the regular expression against the entire, space-separated list of destinations, rather than on each individual destination
- `d` - enables ["dotall"](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/RegExp/dotAll)
- `u` - disables unicode groups

Example: The following switch would output a redstone signal if the user has the `icenia` destination set, or if they have any `icenia_` + anything destinations set, such as `icenia_factory`
```
[destex]
icenia_?\w*



```

### Destination Adding
- Normal: `[destadd]`

Sets additional destinations for the player. Each detail line is considered 1 destination, meaning 1 destadd switch can add 3 destinations. Destinations will not be duplicated: if a player already has a destination set, it will not be set a second time. The switch outputs a redstone signal if any one of the 3 destinations was set.

Example: the following switch would set destinations `icenia`, `ita`, and `alador` for the player.
```
[destadd]
icenia
ita
alador
```

### Destination Removing
- Normal: `[destrm]`

Unsets destinations for the player. Each detail line is considered 1 destination, meaning 1 destrm switch can remove 3 destinations. The switch outputs a redstone signal if any one of the 3 destinations was unset from the player.

Example: the following switch would unset destinations `usa` and `ri` for the player.
```
[destrm]
usa
ri


```

### RegularExpression Destination Removing
- Normal: `[destrmex]` or `[destrmex;<flags>]`

Unsets any destinations matching a regular expression for the player. Regular expression behavior is the same as the RegularExpression Destination Check switch, although the `m` flag is not supported for destination removing. The switch outputs a redstone signal if any destination was unset from the player.

Example: the following switch would unset any destinations starting with `ti_` or containing `temporal`, ignoring case.
```
[destrmex;i]
ti_.*|.*temporal.*



```

## Example Video

[![RailSwitch Demo Video](https://img.youtube.com/vi/GKku2fcB-wY/0.jpg)](https://www.youtube.com/watch?v=GKku2fcB-wY)
