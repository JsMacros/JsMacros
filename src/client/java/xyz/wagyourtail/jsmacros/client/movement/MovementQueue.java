package xyz.wagyourtail.jsmacros.client.movement;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import xyz.wagyourtail.jsmacros.api.PlayerInput;
import xyz.wagyourtail.jsmacros.api.math.Pos3D;
import xyz.wagyourtail.jsmacros.client.api.classes.render.Draw3D;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FHud;

import java.util.ArrayList;
import java.util.List;

import static xyz.wagyourtail.jsmacros.client.JsMacros.LOGGER;

public class MovementQueue {
    private static final List<PlayerInput> queue = new ArrayList<>();
    private static final List<Vec3d> predictions = new ArrayList<>();
    public static Draw3D predPoints = new Draw3D();
    private static ClientPlayerEntity player;
    private static int queuePos = 0;
    private static boolean reCalcPredictions;

    private static boolean doDrawPredictions = false;

    public synchronized static PlayerInput tick(ClientPlayerEntity newPlayer) {
        if (queuePos == queue.size()) {
            return null;
        }

        player = newPlayer;

        if (predictions.size() == queue.size() - queuePos + 1 && queuePos != 0) {
            Vec3d diff = new Vec3d(player.getX() - predictions.get(0).getX(), player.getY() - predictions.get(0).getY(), player.getZ() - predictions.get(0).getZ());
            if (diff.length() > 0.01D) {
                LOGGER.debug("Pred of by x={}, y={}, z={}", diff.getX(), diff.getY(), diff.getZ());
                LOGGER.debug("Player pos x={}, y={}, z={}", player.getX(), player.getY(), player.getZ());
                predPoints.addPoint(player.getX(), player.getY(), player.getZ(), 0.02, 0xFFde070a);
                reCalcPredictions = true;
            } else {
                LOGGER.debug("No Diff");
                predictions.remove(0);
            }
        } else {
            LOGGER.debug("No Pred");
            reCalcPredictions = true;
        }

        if (reCalcPredictions) {
            calcPredictions();
            drawPredictions();
            reCalcPredictions = false;
        }

        if (predictions.size() > 0) {
            LOGGER.debug("Predic pos x={}, y={}, z={}", predictions.get(0).getX(), predictions.get(0).getY(), predictions.get(0).getZ());
        }

        queuePos++;
        return queue.get(queuePos - 1);
    }

    private synchronized static void calcPredictions() {
        List<PlayerInput> toCalc = ImmutableList.copyOf(queue);
        toCalc = toCalc.subList(queuePos, toCalc.size());
        predictions.clear();
        MovementDummy dummy = new MovementDummy(player);
        for (PlayerInput input : toCalc) {
            predictions.add(dummy.applyInput(input));
        }
    }

    private synchronized static void drawPredictions() {
        predictions.forEach(point -> predPoints.addPoint(new Pos3D(point.getX(), point.getY(), point.getZ()), 0.01, 0xFFffd000));
    }

    public static void append(PlayerInput input, ClientPlayerEntity newPlayer) {
        reCalcPredictions = true;
        player = newPlayer;
        // We do the clone step here, since somewhere one could maybe change the input
        // and we don't want that to affect us.
        queue.add(input.clone());
    }

    public synchronized static void setDrawPredictions(boolean val) {
        if (val ^ doDrawPredictions) {
            doDrawPredictions = val;
            if (doDrawPredictions) {
                FHud.renders.add(predPoints);
            } else {
                FHud.renders.remove(predPoints);
            }
        }
    }

    public synchronized static void clear() {
        queue.clear();
        predictions.clear();
        if (FHud.renders.contains(predPoints)) {
            FHud.renders.remove(predPoints);
        }
        predPoints = new Draw3D();
        if (doDrawPredictions) {
            FHud.renders.add(predPoints);
        }
        queuePos = 0;
    }

}
