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
import org.cloudburstmc.protocol.bedrock.codec.v291.serializer.LevelEventSerializer_v291;
import org.cloudburstmc.protocol.bedrock.codec.v291.serializer.LevelSoundEvent1Serializer_v291;
import org.cloudburstmc.protocol.bedrock.codec.v313.serializer.LevelSoundEvent2Serializer_v313;
import org.cloudburstmc.protocol.bedrock.codec.v332.serializer.LevelSoundEventSerializer_v332;
import org.cloudburstmc.protocol.bedrock.codec.v361.serializer.LevelEventGenericSerializer_v361;
import org.cloudburstmc.protocol.bedrock.codec.v448.serializer.AvailableCommandsSerializer_v448;
import org.cloudburstmc.protocol.bedrock.codec.v568.Bedrock_v568;
import org.cloudburstmc.protocol.bedrock.codec.v575.BedrockCodecHelper_v575;
import org.cloudburstmc.protocol.bedrock.codec.v575.Bedrock_v575;
import org.cloudburstmc.protocol.bedrock.codec.v575.serializer.CameraInstructionSerializer_v575;
import org.cloudburstmc.protocol.bedrock.codec.v575.serializer.CameraPresetsSerializer_v575;
import org.cloudburstmc.protocol.bedrock.codec.v575.serializer.PlayerAuthInputSerializer_v575;
import org.cloudburstmc.protocol.bedrock.codec.v575.serializer.UnlockedRecipesSerializer_v575;
import org.cloudburstmc.protocol.bedrock.data.*;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParam;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityDataTypes;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityFlag;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerSlotType;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.TextProcessingEventOrigin;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestActionType;
import org.cloudburstmc.protocol.bedrock.data.skin.*;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.bedrock.transformer.FlagTransformer;
import org.cloudburstmc.protocol.bedrock.transformer.TypeMapTransformer;
import org.cloudburstmc.protocol.common.util.TypeMap;

import java.util.List;
import java.util.function.Supplier;

public class CustomNetworkSettings575 extends Bedrock_v575 {

    public static final Supplier<EncodingSettings> SETTINGS = () -> EncodingSettings.builder()
            .maxByteArraySize(ProxyServer.getInstance().getConfiguration().getNetworkSettings().maxByteArraySize())
            .maxListSize(ProxyServer.getInstance().getConfiguration().getNetworkSettings().maxListSize())
            .maxNetworkNBTSize(ProxyServer.getInstance().getConfiguration().getNetworkSettings().maxNetworkNBTSize())
            .maxItemNBTSize(ProxyServer.getInstance().getConfiguration().getNetworkSettings().maxItemNBTSize())
            .maxStringLength(ProxyServer.getInstance().getConfiguration().getNetworkSettings().maxStringLength())
            .build();

    protected static final TypeMap<Ability> PLAYER_ABILITIES = Bedrock_v568.PLAYER_ABILITIES.toBuilder()
            .insert(18, Ability.PRIVILEGED_BUILDER)
            .build();

    protected static final TypeMap<EntityFlag> ENTITY_FLAGS = Bedrock_v568.ENTITY_FLAGS.toBuilder()
            .insert(110, EntityFlag.SCENTING)
            .insert(111, EntityFlag.RISING)
            .insert(112, EntityFlag.FEELING_HAPPY)
            .insert(113, EntityFlag.SEARCHING)
            .build();

    protected static final TypeMap<ParticleType> PARTICLE_TYPES = Bedrock_v568.PARTICLE_TYPES.toBuilder()
            .insert(85, ParticleType.BRUSH_DUST)
            .build();

    protected static final EntityDataTypeMap ENTITY_DATA = Bedrock_v568.ENTITY_DATA.toBuilder()
            .update(EntityDataTypes.FLAGS, new FlagTransformer(ENTITY_FLAGS, 0))
            .update(EntityDataTypes.FLAGS_2, new FlagTransformer(ENTITY_FLAGS, 1))
            .update(EntityDataTypes.AREA_EFFECT_CLOUD_PARTICLE, new TypeMapTransformer(PARTICLE_TYPES))
            .build();

    protected static final TypeMap<SoundEvent> SOUND_EVENTS = Bedrock_v568.SOUND_EVENTS.toBuilder()
            .replace(462, SoundEvent.BRUSH)
            .insert(463, SoundEvent.BRUSH_COMPLETED)
            .insert(464, SoundEvent.SHATTER_DECORATED_POT)
            .insert(465, SoundEvent.BREAK_DECORATED_POD)
            .insert(466, SoundEvent.UNDEFINED)
            .build();

    protected static final TypeMap<LevelEventType> LEVEL_EVENTS = Bedrock_v568.LEVEL_EVENTS.toBuilder()
            .insert(16384, PARTICLE_TYPES)
            .build();

    protected static final TypeMap<CommandParam> COMMAND_PARAMS = TypeMap.builder(CommandParam.class)
            .insert(0, CommandParam.UNKNOWN)
            .insert(1, CommandParam.INT)
            .insert(3, CommandParam.FLOAT)
            .insert(4, CommandParam.VALUE)
            .insert(5, CommandParam.WILDCARD_INT)
            .insert(6, CommandParam.OPERATOR)
            .insert(7, CommandParam.COMPARE_OPERATOR)
            .insert(8, CommandParam.TARGET)
            .insert(9, CommandParam.UNKNOWN_STANDALONE)
            .insert(10, CommandParam.WILDCARD_TARGET)
            .insert(11, CommandParam.UNKNOWN_NON_ID)
            .insert(12, CommandParam.SCORE_ARG)
            .insert(13, CommandParam.SCORE_ARGS)
            .insert(14, CommandParam.SCORE_SELECT_PARAM)
            .insert(15, CommandParam.SCORE_SELECTOR)
            .insert(16, CommandParam.TAG_SELECTOR)
            .insert(17, CommandParam.FILE_PATH)
            .insert(18, CommandParam.FILE_PATH_VAL)
            .insert(19, CommandParam.FILE_PATH_CONT)
            .insert(20, CommandParam.INT_RANGE_VAL)
            .insert(21, CommandParam.INT_RANGE_POST_VAL)
            .insert(22, CommandParam.INT_RANGE)
            .insert(23, CommandParam.INT_RANGE_FULL)
            .insert(24, CommandParam.SEL_ARGS)
            .insert(25, CommandParam.ARGS)
            .insert(26, CommandParam.ARG)
            .insert(27, CommandParam.MARG)
            .insert(28, CommandParam.MVALUE)
            .insert(29, CommandParam.NAME)
            .insert(30, CommandParam.TYPE)
            .insert(31, CommandParam.FAMILY)
            .insert(32, CommandParam.TAG)
            .insert(33, CommandParam.HAS_ITEM_ELEMENT)
            .insert(34, CommandParam.HAS_ITEM_ELEMENTS)
            .insert(35, CommandParam.HAS_ITEM)
            .insert(36, CommandParam.HAS_ITEMS)
            .insert(37, CommandParam.HAS_ITEM_SELECTOR)
            .insert(38, CommandParam.EQUIPMENT_SLOTS)
            .insert(39, CommandParam.STRING)
            .insert(40, CommandParam.ID_CONT)
            .insert(41, CommandParam.COORD_X_INT)
            .insert(42, CommandParam.COORD_Y_INT)
            .insert(43, CommandParam.COORD_Z_INT)
            .insert(44, CommandParam.COORD_X_FLOAT)
            .insert(45, CommandParam.COORD_Y_FLOAT)
            .insert(46, CommandParam.COORD_Z_FLOAT)
            .insert(47, CommandParam.BLOCK_POSITION)
            .insert(48, CommandParam.POSITION)
            .insert(49, CommandParam.MESSAGE_XP)
            .insert(50, CommandParam.MESSAGE)
            .insert(51, CommandParam.MESSAGE_ROOT)
            .insert(52, CommandParam.POST_SELECTOR)
            .insert(53, CommandParam.TEXT)
            .insert(54, CommandParam.TEXT_CONT)
            .insert(55, CommandParam.JSON_VALUE)
            .insert(56, CommandParam.JSON_FIELD)
            .insert(57, CommandParam.JSON)
            .insert(58, CommandParam.JSON_OBJECT_FIELDS)
            .insert(59, CommandParam.JSON_OBJECT_CONT)
            .insert(60, CommandParam.JSON_ARRAY)
            .insert(61, CommandParam.JSON_ARRAY_VALUES)
            .insert(62, CommandParam.JSON_ARRAY_CONT)
            .insert(63, CommandParam.BLOCK_STATE)
            .insert(64, CommandParam.BLOCK_STATE_KEY)
            .insert(65, CommandParam.BLOCK_STATE_VALUE)
            .insert(66, CommandParam.BLOCK_STATE_VALUES)
            .insert(67, CommandParam.BLOCK_STATES)
            .insert(68, CommandParam.BLOCK_STATES_CONT)
            .insert(69, CommandParam.COMMAND)
            .insert(70, CommandParam.SLASH_COMMAND)
            .build();

    public static final BedrockCodec CODEC = Bedrock_v568.CODEC.toBuilder()
            .raknetProtocolVersion(11)
            .protocolVersion(575)
            .minecraftVersion("1.19.70")
            .helper(() -> new CustomBedrockCodecHelper(ENTITY_DATA, GAME_RULE_TYPES, ITEM_STACK_REQUEST_TYPES, CONTAINER_SLOT_TYPES, PLAYER_ABILITIES, TEXT_PROCESSING_ORIGINS))
            .updateSerializer(LevelSoundEvent1Packet.class, new LevelSoundEvent1Serializer_v291(SOUND_EVENTS))
            .updateSerializer(LevelSoundEvent2Packet.class, new LevelSoundEvent2Serializer_v313(SOUND_EVENTS))
            .updateSerializer(LevelSoundEventPacket.class, new LevelSoundEventSerializer_v332(SOUND_EVENTS))
            .updateSerializer(LevelEventPacket.class, new LevelEventSerializer_v291(LEVEL_EVENTS))
            .updateSerializer(LevelEventGenericPacket.class, new LevelEventGenericSerializer_v361(LEVEL_EVENTS))
            .updateSerializer(PlayerAuthInputPacket.class, new PlayerAuthInputSerializer_v575())
            .updateSerializer(AvailableCommandsPacket.class, new AvailableCommandsSerializer_v448(COMMAND_PARAMS))
            .registerPacket(CameraPresetsPacket::new, new CameraPresetsSerializer_v575(), 198, PacketRecipient.CLIENT)
            .registerPacket(UnlockedRecipesPacket::new, new UnlockedRecipesSerializer_v575(), 199, PacketRecipient.CLIENT)
            .registerPacket(CameraInstructionPacket::new, new CameraInstructionSerializer_v575(), 300, PacketRecipient.CLIENT)
            .build();

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