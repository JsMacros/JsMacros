package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import xyz.wagyourtail.jsmacros.client.access.IBossBarHud;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * @author Wagyourtail
 * @since 1.2.1
 */
public class BossBarHelper extends BaseHelper<BossBar> {

    private BossBarHelper(UUID uuid, Text name, float percentage, BossBar.Color color, BossBar.Style style){
        super(new ClientBossBar(uuid, name, percentage, color, style, false, false, false));
    }

    /**
     * @since 1.8.4
     * @param name Title of the BossBar
     * @param percentage Percentage filled should be between 0 and 1
     * @param color one of the following: "pink", "blue", "red", "green", "yellow", "purple", "white"
     * @param style one of the following: "progress", "notched_6", "notched_10", "notched_12", "notched_20"
     */
    public BossBarHelper(TextHelper name, float percentage, String color, String style){
        this(UUID.randomUUID(), name.getRaw(), percentage, BossBar.Color.byName(color.toUpperCase(Locale.ROOT)), BossBar.Style.byName(style.toUpperCase(Locale.ROOT)));
    }

    /**
     * @since 1.8.4
     * @param uuid Get BossBarHelper from uuid. If this bossbar does not exist it creates a new one
     */
    public BossBarHelper(String uuid){
        this(((IBossBarHud) MinecraftClient.getInstance().inGameHud.getBossBarHud()).jsmacros_GetBossBars().getOrDefault(UUID.fromString(uuid), new ClientBossBar(UUID.fromString(uuid), Text.literal("-"), 0f, BossBar.Color.WHITE, BossBar.Style.PROGRESS, false, false, false)));
    }

    //Default constructor used in the event
    public BossBarHelper(BossBar b) {
        super(b);
    }

    /**
     * Add the Bossbar to the hud if not already
     * @since 1.8.4
     */
    public void add(){
        assert getRaw() instanceof ClientBossBar;
        Map<UUID, ClientBossBar> bars = getBossBars();
        if(!bars.containsKey(getRaw().getUuid())) bars.put(getRaw().getUuid(), (ClientBossBar) getRaw());
    }

    /**
     * Remove the Bossbar from the hud
     * @since 1.8.4
     */
    public void remove(){
        getBossBars().remove(getRaw().getUuid());
    }

    /**
     * Check if a bossbar is already displayed
     * @since 1.8.4
     */
    public boolean isShown(){
        return getBossBars().containsKey(getRaw().getUuid());
    }

    /**
     * Set the Percentage of the bossbar
     * @since 1.8.4
     * @param percentage Percentage of the Bossbar. Should be between 0 and 1
     */
    public void setPercent(float percentage){
        getRaw().setPercent(percentage);
    }

    /**
     * Set the title of the bossbar
     * @since 1.8.4
     * @param name Title of the Hotbar
     */
    public void setName(TextHelper name){
        getRaw().setName(name.getRaw());
    }

    /**
     * Set the title of the bossbar
     * @since 1.8.4
     * @param name Title of the Hotbar
     */
    public void setName(String name){
        getRaw().setName(Text.literal(name));
    }

    /**
     * Set the color of the bossbar
     * @since 1.8.4
     * @param color one of the following: "pink", "blue", "red", "green", "yellow", "purple", "white"
     */
    public void setColor(String color){
        getRaw().setColor(BossBar.Color.byName(color.toLowerCase(Locale.ROOT)));
    }

    /**
     * Set the style of the Bossbar
     * @since 1.8.4
     * @param style one of the following: "progress", "notched_6", "notched_10", "notched_12", "notched_20"
     */
    public void setStyle(String style){
        getRaw().setStyle(BossBar.Style.byName(style.toLowerCase(Locale.ROOT)));
    }


    private Map<UUID, ClientBossBar> getBossBars(){
        assert MinecraftClient.getInstance().inGameHud != null;
        return ((IBossBarHud) MinecraftClient.getInstance().inGameHud.getBossBarHud()).jsmacros_GetBossBars();
    }


    /**
     * @since 1.2.1
     * @return boss bar uuid.
     */
    public String getUUID() {
        return base.getUuid().toString();
    }
    
    /**
     * @since 1.2.1
     * @return percent of boss bar remaining.
     */
    public float getPercent() {
        return base.getPercent();
    }
    
    /**
     * @since 1.2.1
     * @return boss bar color.
     */
    public String getColor() {
        return base.getColor().getName().toUpperCase(Locale.ROOT);
    }
    
    /**
     * @since 1.2.1
     * @return boss bar notch style.
     */
    public String getStyle() {
        return base.getStyle().getName().toUpperCase(Locale.ROOT);
    }

    /**
     * @return the color of this boss bar.
     *
     * @since 1.8.4
     */
    public int getColorValue() {
        Formatting f = base.getColor().getTextFormat();
        return f.getColorValue() == null ? -1 : f.getColorValue();
    }

    /**
     * @return the format of the boss bar's color.
     *
     * @since 1.8.4
     */
    public FormattingHelper getColorFormat() {
        return new FormattingHelper(base.getColor().getTextFormat());
    }
    
    /**
     * @since 1.2.1
     * @return name of boss bar
     */
    public TextHelper getName() {
        return new TextHelper(base.getName());
    }
    
    @Override
    public String toString() {
        return String.format("BossBarHelper:{\"name:\": \"%s\", \"percent\": %f}", base.getName().getString(), base.getPercent());
    }
}