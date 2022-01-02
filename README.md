![ElytraAeronautics](https://media.discordapp.net/attachments/754495868861677628/927260411785539604/ean2.png?width=1289&height=255)

# TLDR; What does this mod do?

This mod has three main features:
1. When flying with elytra, the player will fly faster the higher they fly (similar as in real life, where air density is lower at high altitudes, decreasing air drag).
2. Adds additional cloud layers indicating when fly speed starts to increase and when it ceases to increase, acting as a reference point for height and speed.
3. Features 1 and 2 are FULLY customizable: you can add and setup as many cloud layers as you want, and modify extra flight speed or disable it altogether.

Important: right now both ModMenu and ClothConfig are required to access to the configuration screen.

# More insight into the mod

## Elytra flying
First of all, let me clarify some terms used in the following explanation:
- "Flight speed" refers to elytra flight/glide speed.
- "Cruise speed" refers to flight speed at a constant 0° angle to the ground.

![flyingme](https://media.discordapp.net/attachments/754495868861677628/927264908012302367/best2.png?width=1618&height=910)

If you are like me, a player who likes to explore the world, and travel very far away from the spawn to find unique generation and breathtaking landscapes to build your base and farms around, you might end up with your buildings spreaded across a 30,000-block-wide world.

The average player would be smart and link everything with a nether highway system. But I personally find elytra travelling very enjoyable, and for an endgame item which purpose is to specifically make travelling more convenient, it lacks utility when it is used in long-distance travel. 

The premise behind this mod is to make elytra travel more useful when covering large distances, making it travel at airplane speeds when flying at airplane heights.

When using Elytra Aeronautics with default settings, flight speed increases with altitude following this curve:

![speedcurve](https://media.discordapp.net/attachments/754495868861677628/926964552552300624/eanGraph.png?width=1249&height=910)

As you can see, speed increment is very low from y=0 to y=250. 
This is to not make low altitude flight go easily out of control, again, if you are like me and use the elytra to travel just 20 blocks across your base, you don't have to worry about reaching MACH5 when going for some potatoes. 

Beyond the 250 blocks of altitude mark, flight speed increases exponentially up to its maximum at y=1000. At this altitude, cruise speed is around 250 block/second, matching commercial flight average cruise speed.

## Keeping it vanilla
When developing a mod my intention is to make everything feel as vanilla as possible. When making this one I came across a problem: "How could I make some sort of altimeter and speedometer without adding fancy items or any GUI?" because some kind of guide is needed when flying far up from the ground, and using the debug (F3) screen is not very immersive.

After giving it some thought, I got an idea: using clouds as some kind of reference point for the player's vertical position.

By default Elytra Aeronautics adds two additional cloud layers, one at the middle point of the speed curve (y=250) and another at the end of the speed curve (y=1000); 
these serve as an altitude marker like previously mentioned and as a speed marker, as these will appear moving faster relative to the player when they are travelling at high speeds.

This leads me to the second and third features of the mod, clouds and customization.

## Clouds and customization

Using the config screen you can add as many cloud layers as you wish, and use different cloud types and render distances for each layer individually.

![howdidwegethere](https://cdn.discordapp.com/attachments/754495868861677628/927174474787348530/2022-01-02_13.19.34.png)
_"How did we get here?"_

Furthermore, this mod adds "LOD clouds", these clouds will render as "Fast clouds" when far away from them and as "Fancy clouds" when close to them.

![lodclouds](src/main/resources/assets/elytra_aeronautics/test2.GIF)

You can even make it so when a cloud layer is far away enough from the player it stops rendering, so you can set up hundreds of cloud layers and lose almost no performance.

![unlimitedclouds](postResources/layers.GIF)

As previously mentioned each layer can be configured individually, or all of them at once, giving to the player full access to the additional cloud functionality of the mod.

![cloudconfigscreen](https://media.discordapp.net/attachments/754495868861677628/927180129971601448/unknown.png?width=1290&height=701)

## Real example (Only in github)
Wood land Mansions can easily be more than 20k blocks away from the spawn, sometimes even 50k blocks in large biomes worlds. 

This is a real seed (-962156719158439332, generated in MC 1.16.5) and the nearest mansion is 21898 blocks away.

![examplepic](https://cdn.discordapp.com/attachments/754495868861677628/927030132806418512/example2.png)
_Picture made using [Admist](https://github.com/toolbox4minecraft/amidst)._

![examplescreenshot](https://cdn.discordapp.com/attachments/754495868861677628/927025278998437908/unknown.png)
Taking the closest mansion from spawn (at 21898 blocks of distance) as an example:

The maximum flight speed with elytra in Vanilla Minecraft is around 60 blocks/second, that is when travelling at a 45° angle to the ground, it would take around 6 minutes to travel that distance.

When using Elytra Aeronautics with default settings, flight speed can reach 250 blocks/second when travelling parallel to the horizon, and 500 blocks/seconds when flying at a 45° angle. At horizontal speed it would take 1:27 minutes to travel that distance, and at maximum speed, it would just take 43 seconds to reach the mansion.

Remember, this is using default settings, maximum flight speed can be increased or decreased.

## Integration
- ModMenu (3.0.1 or higher) -> required to access config screen.
- ClothConfig (0.6.0 or higher) -> required to build config screen.

## Compatibility
- Right now there were no compatibility issues found, the mod is still pretty new, if you find any issue, please let me know.  

## Future development

## F.A.Q.

## Contact

## Support

