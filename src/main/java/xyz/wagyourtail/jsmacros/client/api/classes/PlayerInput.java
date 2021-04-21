package xyz.wagyourtail.jsmacros.client.api.classes;

import com.google.gson.Gson;
import net.minecraft.client.input.Input;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An object, that combines all possible player inputs
 *
 * @author NotSomeBot
 * @since 1.4.0
 */
@SuppressWarnings("unused")
public class PlayerInput {
    private static final Gson gson = new Gson();
    public float movementForward;
    public float movementSideways;
    public float yaw;
    public float pitch;
    public boolean jumping;
    public boolean sneaking;
    public boolean sprinting;

    /**
     * Creates a new {@code PlayerInput} Object with all values set either to 0 or false
     *
     * @see #PlayerInput(float, float, float, float, boolean, boolean, boolean)
     * @since 1.4.0
     */
    public PlayerInput() {
        this(0.0F, 0.0F, 0.0F, 0.0F, false, false, false);
    }

    /**
     * Creates a new {@code PlayerInput} Object with all other values set either to 0 or false
     *
     * @param movementForward  1 = forward input (W); 0 = no input; -1 = backward input (S)
     * @param movementSideways 1 = left input (A); 0 = no input; -1 = right input (D)
     * @param yaw              yaw of the player
     * @see #PlayerInput(float, float, float, float, boolean, boolean, boolean)
     * @since 1.4.0
     */
    public PlayerInput(float movementForward, float movementSideways, float yaw) {
        this(movementForward, movementSideways, yaw, 0.0, false, false, false);
    }

    /**
     * Creates a new {@code PlayerInput} Object with all other values set either to 0 or false
     *
     * @param movementForward 1 = forward input (W); 0 = no input; -1 = backward input (S)
     * @param yaw             yaw of the player
     * @param jumping         jump input
     * @param sprinting       sprint input
     * @see #PlayerInput(float, float, float, float, boolean, boolean, boolean)
     * @since 1.4.0
     */
    public PlayerInput(float movementForward, float yaw, boolean jumping, boolean sprinting) {
        this(movementForward, 0.0F, yaw, 0.0, jumping, false, sprinting);
    }

    /**
     * Creates a new {@code PlayerInput} Object from a minecraft input with the missing values extra
     *
     * @param input     Minecraft Input to be converted.
     * @param yaw       yaw of the player
     * @param pitch     pitch of the player
     * @param sprinting sprint input
     * @see #PlayerInput(float, float, float, float, boolean, boolean, boolean)
     * @since 1.4.0
     */
    public PlayerInput(Input input, float yaw, float pitch, boolean sprinting) {
        this(input.movementForward, input.movementSideways, yaw, pitch, input.jumping, input.sneaking, sprinting);
    }

    /**
     * Creates a new {@code PlayerInput} Object with all double values converted to floats
     *
     * @param movementForward  1 = forward input (W); 0 = no input; -1 = backward input (S)
     * @param movementSideways 1 = left input (A); 0 = no input; -1 = right input (D)
     * @param yaw              yaw of the player
     * @param pitch            pitch of the player
     * @param jumping          jump input
     * @param sneaking         sneak input
     * @param sprinting        sprint input
     * @see #PlayerInput(float, float, float, float, boolean, boolean, boolean)
     * @since 1.4.0
     */
    public PlayerInput(double movementForward, double movementSideways, double yaw, double pitch, boolean jumping, boolean sneaking, boolean sprinting) {
        this((float) movementForward, (float) movementSideways, (float) yaw, (float) pitch, jumping, sneaking, sprinting);
    }

    /**
     * Creates a new {@code PlayerInput} Object
     *
     * @param movementForward  1 = forward input (W); 0 = no input; -1 = backward input (S)
     * @param movementSideways 1 = left input (A); 0 = no input; -1 = right input (D)
     * @param yaw              yaw of the player
     * @param pitch            pitch of the player
     * @param jumping          jump input
     * @param sneaking         sneak input
     * @param sprinting        sprint input
     * @since 1.4.0
     */
    public PlayerInput(float movementForward, float movementSideways, float yaw, float pitch, boolean jumping, boolean sneaking, boolean sprinting) {
        this.movementForward = movementForward;
        this.movementSideways = movementSideways;
        this.yaw = yaw;
        this.pitch = pitch;
        this.jumping = jumping;
        this.sneaking = sneaking;
        this.sprinting = sprinting;
    }

    /**
     * Creates a clone {@code PlayerInput} Object
     *
     * @param input the {@code PlayerInput} object to be cloned
     * @since 1.4.0
     */
    public PlayerInput(PlayerInput input) {
        this.movementForward = input.movementForward;
        this.movementSideways = input.movementSideways;
        this.yaw = input.yaw;
        this.pitch = input.pitch;
        this.jumping = input.jumping;
        this.sneaking = input.sneaking;
        this.sprinting = input.sprinting;
    }

    /**
     * Parses each row of CSV string into a {@code PlayerInput}.
     * The capitalization of the header doesn't matter.<br>
     * About the columns:
     * <ul>
     *   <li>Either {@code movementForward} and {@code movementSideways} as a number<br>
     *   OR {@code WASD} each as their own boolean column MUST be present</li>
     *   <li>{@code yaw} and {@code pitch} are if not present defaulted to 0.0</li>
     *   <li>{@code jumping}, {@code sneaking} and {@code sprinting} have to be boolean<br>
     *   and can also be accepted without the {@code ing} at the end</li>
     * </ul>
     * <p>
     * The {@code sep} must be exactly what is between each value, including spaces.<br>
     * Sidenote: macros and recordings from the mod MPK can be used.
     *
     * @param csv CSV string to be parsed
     * @param sep separation between the values
     * @return {@code List<PlayerInput>} Each row parsed as a {@code PlayerInput}
     * @since 1.4.0
     */
    public static List<PlayerInput> fromCsv(String csv, String sep) {
        String[] rows = csv.split("\n");
        List<PlayerInput> output = new ArrayList<>();
        String[] headers = rows[0].split(sep);
        String[] row;
        Map<String, String> mappedRow;
        for (int rowNo = 1; rowNo < rows.length; rowNo++) {
            row = rows[rowNo].split(sep);
            if (row.length == headers.length) {
                mappedRow = new HashMap<>();
                for (int i = 0; i < row.length; i++) {
                    mappedRow.put(headers[i].toLowerCase(), row[i]);
                }
                output.add(fromMap(mappedRow));
            }
        }
        return output;

    }

    /**
     * Parses a JSON string into a {@code PlayerInput} Object
     * For details see {@code PlayerInput.fromCsv()}, on what has to be present.<br>
     * Capitalization of the keys doesn't matter.
     *
     * @param json JSON string to be parsed
     * @return The JSON parsed into a {@code PlayerInput}
     * @see #fromCsv(String, String)
     * @since 1.4.0
     */
    public static PlayerInput fromJson(String json) {
        Map<String, String> map = new HashMap<>();
        map = (Map<String, String>) gson.fromJson(json, map.getClass());
        map = map.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().toLowerCase().replace("\"", ""), e -> e.getValue().replace("\"", "")));
        return fromMap(map);
    }


    /**
     * Converts a Map, which has all keys lowercase and that contains all the prerequisites listed in {@code PlayerInput.fromCSV}.
     *
     * @param input Map to be converted
     * @return The map converted into a {@code PlayerInput}
     * @see #fromCsv(String, String)
     * @since 1.4.0
     */
    private static PlayerInput fromMap(Map<String, String> input) {
        return new PlayerInput(
                input.get("movementforward") != null ? Float.parseFloat(input.get("movementforward")) : input.get("w").equals(input.get("s")) ? 0.0F : (Boolean.parseBoolean(input.get("w")) ? 1.0F : -1.0F),
                input.get("movementsideways") != null ? Float.parseFloat(input.get("movementsideways")) : input.get("a").equals(input.get("d")) ? 0.0F : (Boolean.parseBoolean(input.get("a")) ? 1.0F : -1.0F),
                input.get("yaw") != null ? Float.parseFloat(input.get("yaw")) : 0.0f,
                input.get("pitch") != null ? Float.parseFloat(input.get("pitch")) : 0.0f,
                input.get("jumping") != null ? Boolean.parseBoolean(input.get("jumping")) : input.get("jump") != null && Boolean.parseBoolean(input.get("jump")),
                input.get("sneaking") != null ? Boolean.parseBoolean(input.get("sneaking")) : input.get("sneak") != null && Boolean.parseBoolean(input.get("sneak")),
                input.get("sprinting") != null ? Boolean.parseBoolean(input.get("sprinting")) : input.get("sprint") != null && Boolean.parseBoolean(input.get("sprint"))
        );
    }

    /**
     * Converts the current object into a string.
     * This can be used to convert current inputs created using {@code Player.getCurrentPlayerInput()}
     * to either JSON or CSV.
     * <p>
     * The output can be converted into a PlayerInput object again by using either
     * {@code fromCsv(String, String)} or {@code fromJson(String)}.
     *
     * @param wasd     whether to convert {@code movementForward/Sideways} into {@code WASD} values
     * @param varNames whether to include variable Names(=JSON) or not(=CSV)
     * @return The {@code PlayerInput} object as a string
     * @since 1.4.0
     */
    public String toString(boolean wasd, boolean varNames) {
        StringBuilder stringBuilder = new StringBuilder();
        if (wasd) {
            stringBuilder.append(varNames ? "\"W\": " : "");
            stringBuilder.append(this.movementForward == 1.0F ? "true" : "false");
            stringBuilder.append(varNames ? " \"A\": " : ", ");
            stringBuilder.append(this.movementSideways == 1.0F ? "true" : "false");
            stringBuilder.append(varNames ? " \"S\": " : ", ");
            stringBuilder.append(this.movementForward == -1.0F ? "true" : "false");
            stringBuilder.append(varNames ? " \"D\": " : ", ");
            stringBuilder.append(this.movementSideways == -1.0F ? "true" : "false");
            stringBuilder.append(", ");
        }

        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (wasd && (field.getName().equals("movementForward") || field.getName().equals("movementSideways"))) {
                continue;
            }

            if (varNames) {
                stringBuilder.append("\"");
                stringBuilder.append(field.getName());
                stringBuilder.append("\": ");
            }

            try {
                if (field.getType().equals(float.class)) {
                    stringBuilder.append(field.getFloat(this));
                } else {
                    stringBuilder.append(field.get(this).toString());
                }

            } catch (IllegalAccessException ignored) {
            }
            stringBuilder.append(", ");
        }
        stringBuilder.setLength(stringBuilder.length() - 2);
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return "PlayerInput{" +
                "movementForward=" + movementForward +
                ", movementSideways=" + movementSideways +
                ", yaw=" + yaw +
                ", pitch=" + pitch +
                ", jumping=" + jumping +
                ", sneaking=" + sneaking +
                ", sprinting=" + sprinting +
                '}';
    }

    @Override
    public PlayerInput clone() {
        return new PlayerInput(this);
    }
}
