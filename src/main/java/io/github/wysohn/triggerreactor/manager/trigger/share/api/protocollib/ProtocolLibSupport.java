package io.github.wysohn.triggerreactor.manager.trigger.share.api.protocollib;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import io.github.wysohn.triggerreactor.main.TriggerReactor;
import io.github.wysohn.triggerreactor.manager.trigger.share.api.APISupport;
import io.github.wysohn.triggerreactor.manager.trigger.share.api.APISupportException;
import io.github.wysohn.triggerreactor.tools.ValidationUtil;

public class ProtocolLibSupport extends APISupport {
    private ProtocolManager protocolManager;

    public ProtocolLibSupport(TriggerReactor plugin) {
        super(plugin, "ProtocolLib");
    }

    @Override
    public void init() throws APISupportException {
        super.init();

        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    /**
     * Create empty packet.
     * @param packetType type of packet as String. Should match with any
     * <a href="https://aadnk.github.io/ProtocolLib/Javadoc/com/comphenix/protocol/package-frame.html">PacketType</a>.
     * For example, ENTITY is under PacketType.Play.Server.
     * @return
     */
    public PacketContainer createPacket(String packetType){
        ValidationUtil.notNull(packetType);

        Collection<PacketType> types = PacketType.fromName(packetType);
        if(types.isEmpty())
            throw new RuntimeException("Cannot find packet type "+packetType);

        PacketType type = types.iterator().next();
        return protocolManager.createPacket(type);
    }

    /**
     * create empty data watcher. Use this for fake entities.
     * @return
     */
    public WrappedDataWatcher createEmptyWatcher(){
        return new WrappedDataWatcher();
    }

    /**
     * send packet.
     * @param p player to send packet
     * @param container the packet
     * @throws InvocationTargetException
     */
    public void sendPacket(Player p, PacketContainer container) throws InvocationTargetException{
        ValidationUtil.notNull(p);
        ValidationUtil.notNull(container);

        ProtocolLibrary.getProtocolManager().sendServerPacket(p, container);
    }

    /**
     * Predefined method to spawn a fake entity. It may not work in versions older (perhaps newer) versions than 1.12
     * @param p player to send packet
     * @param entityId entity id to use. Usually you use negative number to distinguish entity id with real entities.
     * @param entityUuid uuid of entity
     * @param type type number of <a href="http://wiki.vg/Entities#Mobs">entity</a>.
     * @param x
     * @param y
     * @param z
     * @param yaw
     * @param pitch
     * @param headPitch
     * @param velX See <a href="http://wiki.vg/Protocol#Entity_Velocity">this</a>
     * @param velY See <a href="http://wiki.vg/Protocol#Entity_Velocity">this</a>
     * @param velZ See <a href="http://wiki.vg/Protocol#Entity_Velocity">this</a>
     * @throws InvocationTargetException
     */
    public void sendEntitySpawn(Player p, int entityId, UUID entityUuid, int type,
            double x, double y, double z, double yaw, double pitch, double headPitch,
            int velX, int velY, int velZ) throws InvocationTargetException{
        PacketContainer container = createPacket(PacketType.Play.Server.ENTITY.name());

        container.getIntegers()
            .write(0, entityId)
            .write(1, type);

        container.getUUIDs()
            .write(0, entityUuid == null ? UUID.randomUUID() : entityUuid);

        container.getDoubles()
            .write(0, x)
            .write(1, y)
            .write(2, z);

        container.getFloat()
            .write(0, (float) yaw)
            .write(1, (float) pitch)
            .write(2, (float) headPitch);

        container.getShorts()
            .write(0, (short) velX)
            .write(1, (short) velX)
            .write(2, (short) velX);

        container.getDataWatcherModifier()
            .write(0, createEmptyWatcher());

        this.sendPacket(p, container);
    }

    /**
     * Spawn fake entity without velocity set and with random UUID set.
     * @param p player to send packet
     * @param entityId entity id to use. Usually you use negative number to distinguish entity id with real entities.
     * @param type type number of <a href="http://wiki.vg/Entities#Mobs">entity</a>.
     * @param x
     * @param y
     * @param z
     * @param yaw
     * @param pitch
     * @param headPitch
     * @throws InvocationTargetException
     */
    public void sendEntitySpawn(Player p, int entityId, int type,
            double x, double y, double z, double yaw, double pitch, double headPitch) throws InvocationTargetException{
        this.sendEntitySpawn(p, entityId, UUID.randomUUID(), type, x, y, z, yaw, pitch, headPitch, 0, 0, 0);
    }

    /**
     * Spawn entity at location
     * @param p player to send packet
     * @param entityId entity id to use. Usually you use negative number to distinguish entity id with real entities.
     * @param type type number of <a href="http://wiki.vg/Entities#Mobs">entity</a>.
     * @param location the Location
     * @throws InvocationTargetException
     */
    public void sendEntitySpawn(Player p, int entityId, int type,
            Location location) throws InvocationTargetException{
        this.sendEntitySpawn(p, entityId, type,
                location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), location.getPitch());
    }

    /**
     * Spawn entity at location
     * @param p player to send packet
     * @param entityId entity id to use. Usually you use negative number to distinguish entity id with real entities.
     * @param type type number of <a href="http://wiki.vg/Entities#Mobs">entity</a>.
     * @param x
     * @param y
     * @param z
     * @throws InvocationTargetException
     */
    public void sendEntitySpawn(Player p, int entityId, int type,
            double x, double y, double z) throws InvocationTargetException{
        this.sendEntitySpawn(p, entityId, type, x, y, z, 0, 0, 0);
    }

    /**
     * Spawn entity at player's current location.
     * @param p player to send packet
     * @param entityId entity id to use. Usually you use negative number to distinguish entity id with real entities.
     * @param type type number of <a href="http://wiki.vg/Entities#Mobs">entity</a>.
     * @throws InvocationTargetException
     */
    public void sendEntitySpawn(Player p, int entityId, int type) throws InvocationTargetException{
        this.sendEntitySpawn(p, entityId, type, p.getLocation());
    }

    /**
     * Send destroy packet for list of entities.
     * @param p player to send packet
     * @param entityId entity ids.
     * @throws InvocationTargetException
     */
    public void sendEntitiesDestroy(Player p, Object[] entityId) throws InvocationTargetException{
        PacketContainer container = createPacket(PacketType.Play.Server.ENTITY_DESTROY.name());

        container.getIntegers().write(0, entityId.length);

        int[] intArray = new int[entityId.length];
        for(int i = 0; i < intArray.length; i++){
            if(!(entityId[i] instanceof Integer))
                throw new RuntimeException("Only integers are allowed in the array.");

            intArray[i] = (int) entityId[i];
        }

        container.getIntegerArrays().write(0, intArray);

        this.sendPacket(p, container);
    }

    /**
     * Send destroy packet for an entity.
     * @param p player to send packet
     * @param entityId entity id
     * @throws InvocationTargetException
     */
    public void sendEntityDestroy(Player p, int entityId) throws InvocationTargetException{
        sendEntitiesDestroy(p, new Object[]{entityId});
    }

    /**
     * send entity movement packet.
     * @param p player to send packet
     * @param entityId entity id of entity to move
     * @param dX delta x
     * @param dY delta y
     * @param dZ delta z
     * @param onGround no documentation
     * @throws InvocationTargetException
     */
    public void sendEntityMove(Player p, int entityId,
            int dX, int dY, int dZ,
            boolean onGround) throws InvocationTargetException{
        PacketContainer container = createPacket(PacketType.Play.Server.REL_ENTITY_MOVE.name());

        container.getIntegers().write(0, entityId);

        container.getShorts()
            .write(0, (short) dX)
            .write(1, (short) dY)
            .write(2, (short) dZ);

        container.getBooleans().write(0, onGround);

        this.sendPacket(p, container);
    }

    /**
     * send entity movement packet.
     * @param p player to send packet
     * @param entityId entity id of entity to move
     * @param fromX
     * @param fromY
     * @param fromZ
     * @param toX
     * @param toY
     * @param toZ
     * @param onGround no documentation
     * @throws InvocationTargetException
     */
    public void sendEntityMove(Player p, int entityId,
            double fromX, double fromY, double fromZ,
            double toX, double toY, double toZ,
            boolean onGround) throws InvocationTargetException{

        int dX = (int) ((fromX * 32 - toZ * 32) * 128);
        int dY = (int) ((fromY * 32 - toY * 32) * 128);
        int dZ = (int) ((fromZ * 32 - toZ * 32) * 128);

        this.sendEntityMove(p, entityId, dX, dY, dZ, onGround);
    }


}
