/*
 * Copyright 2024 WaterdogTEAM
 * Licensed under the GNU General Public License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.waterdog.waterdogpe.custom;

import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.network.protocol.ProtocolVersion;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.EntityDataTypeMap;
import org.cloudburstmc.protocol.bedrock.codec.v291.serializer.LevelSoundEvent1Serializer_v291;
import org.cloudburstmc.protocol.bedrock.codec.v313.serializer.LevelSoundEvent2Serializer_v313;
import org.cloudburstmc.protocol.bedrock.codec.v332.serializer.LevelSoundEventSerializer_v332;
import org.cloudburstmc.protocol.bedrock.codec.v361.serializer.LevelEventGenericSerializer_v361;
import org.cloudburstmc.protocol.bedrock.codec.v575.BedrockCodecHelper_v575;
import org.cloudburstmc.protocol.bedrock.codec.v594.serializer.AvailableCommandsSerializer_v594;
import org.cloudburstmc.protocol.bedrock.codec.v649.Bedrock_v649;
import org.cloudburstmc.protocol.bedrock.codec.v662.Bedrock_v662;
import org.cloudburstmc.protocol.bedrock.codec.v662.serializer.*;
import org.cloudburstmc.protocol.bedrock.data.*;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParam;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerSlotType;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.TextProcessingEventOrigin;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestActionType;
import org.cloudburstmc.protocol.bedrock.data.skin.*;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.common.util.TypeMap;

import java.util.List;
import java.util.function.Supplier;

public class CustomNetworkSettings662 extends Bedrock_v662 {

    public static final Supplier<EncodingSettings> SETTINGS = () -> EncodingSettings.builder()
            .maxByteArraySize(ProxyServer.getInstance().getConfiguration().getNetworkSettings().maxByteArraySize())
            .maxListSize(ProxyServer.getInstance().getConfiguration().getNetworkSettings().maxListSize())
            .maxNetworkNBTSize(ProxyServer.getInstance().getConfiguration().getNetworkSettings().maxNetworkNBTSize())
            .maxItemNBTSize(ProxyServer.getInstance().getConfiguration().getNetworkSettings().maxItemNBTSize())
            .maxStringLength(ProxyServer.getInstance().getConfiguration().getNetworkSettings().maxStringLength())
            .build();

    protected static final TypeMap<CommandParam> COMMAND_PARAMS = Bedrock_v649.COMMAND_PARAMS.toBuilder()
            .remove(134217728)
            .shift(24, 4)
            .insert(24, CommandParam.RATIONAL_RANGE_VAL)
            .insert(25, CommandParam.RATIONAL_RANGE_POST_VAL)
            .insert(26, CommandParam.RATIONAL_RANGE)
            .insert(27, CommandParam.RATIONAL_RANGE_FULL)
            .shift(48, 8)
            .insert(48, CommandParam.PROPERTY_VALUE)
            .insert(49, CommandParam.HAS_PROPERTY_PARAM_VALUE)
            .insert(50, CommandParam.HAS_PROPERTY_PARAM_ENUM_VALUE)
            .insert(51, CommandParam.HAS_PROPERTY_ARG)
            .insert(52, CommandParam.HAS_PROPERTY_ARGS)
            .insert(53, CommandParam.HAS_PROPERTY_ELEMENT)
            .insert(54, CommandParam.HAS_PROPERTY_ELEMENTS)
            .insert(55, CommandParam.HAS_PROPERTY_SELECTOR)
            .insert(134217728, CommandParam.CHAINED_COMMAND)
            .build();
    protected static final TypeMap<ParticleType> PARTICLE_TYPES = Bedrock_v649.PARTICLE_TYPES.toBuilder()
            .replace(18, ParticleType.BREEZE_WIND_EXPLOSION)
            .insert(90, ParticleType.VAULT_CONNECTION)
            .insert(91, ParticleType.WIND_EXPLOSION).build();
    protected static final TypeMap<LevelEventType> LEVEL_EVENTS = Bedrock_v649.LEVEL_EVENTS.toBuilder()
            .replace(3610, LevelEvent.PARTICLE_BREEZE_WIND_EXPLOSION)
            .replace(3614, LevelEvent.PARTICLE_WIND_EXPLOSION)
            .insert(3615, LevelEvent.ALL_PLAYERS_SLEEPING)
            .insert(9811, LevelEvent.ANIMATION_VAULT_ACTIVATE)
            .insert(9812, LevelEvent.ANIMATION_VAULT_DEACTIVATE)
            .insert(9813, LevelEvent.ANIMATION_VAULT_EJECT_ITEM)
            .insert(16384, PARTICLE_TYPES).build();
    protected static final TypeMap<TextProcessingEventOrigin> TEXT_PROCESSING_ORIGINS = Bedrock_v649.TEXT_PROCESSING_ORIGINS.toBuilder()
            .replace(14, TextProcessingEventOrigin.SERVER_FORM)
            .build();
    protected static final TypeMap<SoundEvent> SOUND_EVENTS = Bedrock_v649.SOUND_EVENTS.toBuilder()
            .replace(500, SoundEvent.VAULT_OPEN_SHUTTER)
            .insert(501, SoundEvent.VAULT_CLOSE_SHUTTER)
            .insert(502, SoundEvent.VAULT_EJECT_ITEM)
            .insert(503, SoundEvent.VAULT_INSERT_ITEM)
            .insert(504, SoundEvent.VAULT_INSERT_ITEM_FAIL)
            .insert(505, SoundEvent.VAULT_AMBIENT)
            .insert(506, SoundEvent.VAULT_ACTIVATE)
            .insert(507, SoundEvent.VAULT_DEACTIVATE)
            .insert(508, SoundEvent.HURT_REDUCED)
            .insert(509, SoundEvent.WIND_CHARGE_BURST)
            .insert(511, SoundEvent.UNDEFINED)
            .build();

    public static final BedrockCodec CODEC = Bedrock_v649.CODEC.toBuilder()
            .raknetProtocolVersion(11)
            .protocolVersion(662)
            .minecraftVersion("1.20.70")
            .helper(() -> new CustomBedrockCodecHelper(ENTITY_DATA, GAME_RULE_TYPES, ITEM_STACK_REQUEST_TYPES, CONTAINER_SLOT_TYPES, PLAYER_ABILITIES, TEXT_PROCESSING_ORIGINS))
            .updateSerializer(LevelEventGenericPacket.class, new LevelEventGenericSerializer_v361(LEVEL_EVENTS))
            .updateSerializer(LevelSoundEvent1Packet.class, new LevelSoundEvent1Serializer_v291(SOUND_EVENTS))
            .updateSerializer(LevelSoundEvent2Packet.class, new LevelSoundEvent2Serializer_v313(SOUND_EVENTS))
            .updateSerializer(LevelSoundEventPacket.class, new LevelSoundEventSerializer_v332(SOUND_EVENTS))
            .updateSerializer(AvailableCommandsPacket.class, new AvailableCommandsSerializer_v594(COMMAND_PARAMS))
            .updateSerializer(LecternUpdatePacket.class, LecternUpdateSerializer_v662.INSTANCE)
            .updateSerializer(MobEffectPacket.class, MobEffectSerializer_v662.INSTANCE)
            .updateSerializer(PlayerAuthInputPacket.class, new PlayerAuthInputSerializer_v662())
            .updateSerializer(ResourcePacksInfoPacket.class, ResourcePacksInfoSerializer_v622.INSTANCE)
            .updateSerializer(SetEntityMotionPacket.class, SetEntityMotionSerializer_v662.INSTANCE)
            .deregisterPacket(ItemFrameDropItemPacket.class).build();

    private static class CustomBedrockCodecHelper extends BedrockCodecHelper_v575 {
        public final int CUSTOM_MODEL_SIZE = ProxyServer.getInstance().getConfiguration().getNetworkSettings().maxSkinLength();

        public CustomBedrockCodecHelper(EntityDataTypeMap entityData, TypeMap<Class<?>> gameRulesTypes, TypeMap<ItemStackRequestActionType> stackRequestActionTypes, TypeMap<ContainerSlotType> containerSlotTypes, TypeMap<Ability> abilities, TypeMap<TextProcessingEventOrigin> textProcessingEventOrigins) {
            super(entityData, gameRulesTypes, stackRequestActionTypes, containerSlotTypes, abilities, textProcessingEventOrigins);
        }

        @Override
        public AnimationData readAnimationData(ByteBuf buffer) {
            ImageData image = this.readImage(buffer, CUSTOM_MODEL_SIZE);
            AnimatedTextureType textureType = TEXTURE_TYPES[buffer.readIntLE()];
            float frames = buffer.readFloatLE();
            AnimationExpressionType expressionType = EXPRESSION_TYPES[buffer.readIntLE()];
            return new AnimationData(image, textureType, frames, expressionType);
        }

        @Override
        public SerializedSkin readSkin(ByteBuf buffer) {
            String skinId = this.readString(buffer);
            String playFabId = this.readString(buffer);
            String skinResourcePatch = this.readString(buffer);
            ImageData skinData = this.readImage(buffer, CUSTOM_MODEL_SIZE);

            List<AnimationData> animations = new ObjectArrayList<>();
            this.readArray(buffer, animations, ByteBuf::readIntLE, (b, h) -> this.readAnimationData(b));

            ImageData capeData = this.readImage(buffer, CUSTOM_MODEL_SIZE);
            String geometryData = this.readString(buffer);
            String geometryDataEngineVersion = this.readString(buffer);
            String animationData = this.readString(buffer);
            String capeId = this.readString(buffer);
            String fullSkinId = this.readString(buffer);
            String armSize = this.readString(buffer);
            String skinColor = this.readString(buffer);

            List<PersonaPieceData> personaPieces = new ObjectArrayList<>();
            this.readArray(buffer, personaPieces, ByteBuf::readIntLE, (buf, h) -> {
                String pieceId = this.readString(buf);
                String pieceType = this.readString(buf);
                String packId = this.readString(buf);
                boolean isDefault = buf.readBoolean();
                String productId = this.readString(buf);
                return new PersonaPieceData(pieceId, pieceType, packId, isDefault, productId);
            });

            List<PersonaPieceTintData> tintColors = new ObjectArrayList<>();
            this.readArray(buffer, tintColors, ByteBuf::readIntLE, (buf, h) -> {
                String pieceType = this.readString(buf);
                List<String> colors = new ObjectArrayList<>();
                int colorsLength = buf.readIntLE();
                for (int i2 = 0; i2 < colorsLength; i2++) {
                    colors.add(this.readString(buf));
                }
                return new PersonaPieceTintData(pieceType, colors);
            });

            boolean premium = buffer.readBoolean();
            boolean persona = buffer.readBoolean();
            boolean capeOnClassic = buffer.readBoolean();
            boolean primaryUser = buffer.readBoolean();
            boolean overridingPlayerAppearance = buffer.readBoolean();

            return SerializedSkin.of(skinId, playFabId, skinResourcePatch, skinData, animations, capeData, geometryData, geometryDataEngineVersion,
                    animationData, premium, persona, capeOnClassic, primaryUser, capeId, fullSkinId, armSize, skinColor, personaPieces, tintColors,
                    overridingPlayerAppearance);
        }
    }
}