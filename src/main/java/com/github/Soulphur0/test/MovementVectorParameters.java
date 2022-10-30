package com.github.Soulphur0.test;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * @see <a href="https://docs.google.com/spreadsheets/d/1M9V4Rn18zhw8LtKhFGyUJxN8YXPKszAVx1BnVu2qptQ/edit#gid=0">fallSpeedMultiplier quadratic increase with pitch.</a>
 * */
public class MovementVectorParameters {

    static public Vec3d calcMovementVector(LivingEntity player, double playerAltitude){
        // ? The movement speed constant.
        double speedConstant = 0.08;

        // ? Aux variable used in movement vector calculations
        double aux;
        
        // ? Player's movement vector in Vec3d form. (AKA velocity vector) 
        Vec3d movementVector = player.getVelocity();

        // ? Reset fall distance if flight speed is upwards.
        if (movementVector.y > -0.5) {
            player.fallDistance = 1.0f;
        }
        
        // ? Player's rotation vector.
        Vec3d rotationVector = player.getRotationVector();

        // ? Player's pitch in radians.
        float pitchInRadians = player.getPitch() * ((float)Math.PI / 180);
        // vAngle = 0   ; pitch = 0.008893194; pitchInRadians = 0.0001
        // vAngle = 90  ; pitch = 90         ; pitchInRadians = 1.57
        // vAngle = -90 ; pitch = -90        ; pitchInRadians = -1.57
        /**/

        // ? Tilt or Horizontal angle. Mainly used to determine if the player is not looking straight up/down.
        double angleToTheGround = Math.sqrt(rotationVector.x * rotationVector.x + rotationVector.z * rotationVector.z);
        // pitch = 0  ; var = 1
        // pitch = 90 ; var = 0
        // pitch = -90 ; var = 0~
        /**/
        
        // ? Player flight speed.
        double speed = movementVector.horizontalLength();
        // pitch = 0.0  ; var = 1.51
        // pitch = 45.0 ; var = 3.22
        /**/

        // ! This value is usually 1.0, seems like it exists to handle exceptions in rotation.
        double rotationVectorLength = rotationVector.length();
        // pitch = 0;  rotationVectorLength = 1.0~
        // pitch = 45; rotationVectorLength = 1.0
        // pitch = 90; rotationVectorLength = 1.0~
        /**/

        // ? Similar to angleToTheGround. Cosine of the pitch in radians (0-1). The greater the pitch angle the higher this value.
        float fallSpeedMultiplier = MathHelper.cos(pitchInRadians);
        // pitch = 0  ; pitchInRadians = 0.000 ; fallSpeedMultiplier = 1
        // pitch = 45 ; pitchInRadians = 0.785 ; fallSpeedMultiplier = 0.5
        // pitch = 90 ; pitchInRadians = 1.570 ; fallSpeedMultiplier = 0
        /**/

        // * This makes fallSpeedMultiplier increase with pitch quadratically.
        fallSpeedMultiplier = (float)((double)fallSpeedMultiplier * ((double)fallSpeedMultiplier * Math.min(1.0, rotationVectorLength / 0.4)));
        // See javadoc to see curve.

        // * Adds downwards speed to the movement vector based on the pitch of the player.
        movementVector = player.getVelocity().add(0.0, speedConstant * (-1.0 + (double)fallSpeedMultiplier * 0.75), 0.0);
        //  At max pitch (90degrees) added downwards speed is (-1.0 + 0.0) = -1.0 (maximum down speed).
        //  At min pitch (0 degrees) added downwards speed is (-1.0 + (1.0 * 0.75)) = -0.25 (minimum down speed).

        // * Called if vertical speed is negative(so this is not called when harnessing upwards momentum, i.e. making a concave glide path).
        // * (Player is descending) ->
        // *                            [Determines the rate at which downwards speed should increase when not looking directly to the ground or above the horizon]
        // *                            [Determines horizontal speed based on vertical speed (the effect of being accelerated by gravity and redirecting said force with wings horizontally)]
        if (movementVector.y < 0.0 && angleToTheGround > 0.0) {
            // ! IMPORTANT: movementVector.y determines horizontal speed. Be careful on how this value is added to the y-axis each tick.
            aux = movementVector.y * -0.1 * (double)fallSpeedMultiplier;

            /*
            System.out.println("X speed = " + rotationVector.x * aux / angleToTheGround);
            System.out.println("Z speed = " + rotationVector.z * aux / angleToTheGround);
            System.out.println("Vertical speed = " + aux);
            */

            // Add to each axis of the movement vector the previous value multiplied by the axis' rotation, divided by the angleToTheGround
            movementVector = movementVector.add(rotationVector.x * aux / angleToTheGround, aux, rotationVector.z * aux / angleToTheGround);
        }

        // * Called if the player is aiming over the horizon.
        // *    (Pitch is negative) -> [Speed should decrease]
        // *    Also applies gravity while fliying.
        if (pitchInRadians < 0.0f && angleToTheGround > 0.0) {
            aux = speed * (double)(-MathHelper.sin(pitchInRadians)) * 0.04;

            // Aux is then added to the movement vector, which subtracts speed in each axis depending on how up the player looks (angle to the ground divider is 1 if looking straight up).
            movementVector = movementVector.add(-rotationVector.x * aux / angleToTheGround, aux * 3.2, -rotationVector.z * aux / angleToTheGround);
        }

        // * Always called. Cancels sideways momentum.
        if (angleToTheGround > 0.0) {
            // Add to the movement vector, the 3d looking direction multiplied by the speed minus the horizontal speed divided by 10.
            // In other words, this cancels "natural" sideways momentum, so you can make tight turns quickly.
            movementVector = movementVector.add((rotationVector.x / angleToTheGround * speed - movementVector.x) * 0.1, 0.0, (rotationVector.z / angleToTheGround * speed - movementVector.z) * 0.1);
        }

        return movementVector;
    }
}
