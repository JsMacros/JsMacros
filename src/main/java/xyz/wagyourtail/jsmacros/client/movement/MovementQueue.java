package xyz.wagyourtail.jsmacros.client.movement;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import xyz.wagyourtail.jsmacros.client.api.classes.Draw3D;
import xyz.wagyourtail.jsmacros.client.api.classes.PlayerInput;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FHud;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon;

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

    public static PlayerInput tick(ClientPlayerEntity newPlayer) {
        if (queuePos == queue.size()) {
            return null;
        }

        player = newPlayer;

        Vec3d diff;
        if (predictions.size() > queuePos) {
            diff = new Vec3d(player.getX() - predictions.get(0).getX(), player.getY() - predictions.get(0).getY(), player.getZ() - predictions.get(0).getZ());
            if (diff.length() > 0.01D) {
                LOGGER.debug("Pred of by x={}, y={}, z={}", diff.getX(), diff.getY(), diff.getZ());
                LOGGER.debug("Player pos x={}, y={}, z={}", player.getX(), player.getY(), player.getZ());
                predPoints.addPoint(player.getX(), player.getY(), player.getZ(), 0.02, 0xde070a);
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

        if (predictions.size() > 0)
            LOGGER.debug("Predic pos x={}, y={}, z={}", predictions.get(0).getX(), predictions.get(0).getY(), predictions.get(0).getZ());

        queuePos++;
        return queue.get(queuePos - 1);
    }

    private static void calcPredictions() {
        List<PlayerInput> toCalc = new ArrayList<>(queue.subList(queuePos, queue.size()));
        predictions.clear();
        MovementDummy dummy = new MovementDummy(player);
        for (PlayerInput input : toCalc) {
            predictions.add(dummy.applyInput(input));
        }
    }

    private static void drawPredictions() {
        predictions.forEach(point -> predPoints.addPoint(new PositionCommon.Pos3D(point.getX(), point.getY(), point.getZ()), 0.01, 0xffd000));
    }

    public static void append(PlayerInput input, ClientPlayerEntity newPlayer) {
        reCalcPredictions = true;
        player = newPlayer;
        // We do the clone step here, since somewhere one could maybe change the input
        // and we don't want that to affect us.
        queue.add(input.clone());
    }

    public static void clear() {
        queue.clear();
        predictions.clear();
        if (FHud.renders.contains(predPoints)) {
            synchronized (FHud.renders) {
                FHud.renders.remove(predPoints);
            }
        }
        predPoints = new Draw3D();
        synchronized (FHud.renders) {
            FHud.renders.add(predPoints);
        }
        queuePos = 0;
    }
}
