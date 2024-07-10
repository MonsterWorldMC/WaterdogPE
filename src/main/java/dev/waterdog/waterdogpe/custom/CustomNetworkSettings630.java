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
import org.cloudburstmc.protocol.bedrock.codec.v575.BedrockCodecHelper_v575;
import org.cloudburstmc.protocol.bedrock.codec.v622.Bedrock_v622;
import org.cloudburstmc.protocol.bedrock.codec.v630.Bedrock_v630;
import org.cloudburstmc.protocol.bedrock.codec.v630.serializer.SetPlayerInventoryOptionsSerializer_v360;
import org.cloudburstmc.protocol.bedrock.codec.v630.serializer.ShowStoreOfferSerializer_v630;
import org.cloudburstmc.protocol.bedrock.codec.v630.serializer.ToggleCrafterSlotRequestSerializer_v630;
import org.cloudburstmc.protocol.bedrock.data.*;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityDataTypes;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerSlotType;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.TextProcessingEventOrigin;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestActionType;
import org.cloudburstmc.protocol.bedrock.data.skin.*;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.bedrock.transformer.TypeMapTransformer;
import org.cloudburstmc.protocol.common.util.TypeMap;

import java.util.List;
import java.util.function.Supplier;

public class CustomNetworkSettings630 extends Bedrock_v630 {

    public static final Supplier<EncodingSettings> SETTINGS = () -> EncodingSettings.builder()
            .maxByteArraySize(ProxyServer.getInstance().getConfiguration().getNetworkSettings().maxByteArraySize())
            .maxListSize(ProxyServer.getInstance().getConfiguration().getNetworkSettings().maxListSize())
            .maxNetworkNBTSize(ProxyServer.getInstance().getConfiguration().getNetworkSettings().maxNetworkNBTSize())
            .maxItemNBTSize(ProxyServer.getInstance().getConfiguration().getNetworkSettings().maxItemNBTSize())
            .maxStringLength(ProxyServer.getInstance().getConfiguration().getNetworkSettings().maxStringLength())
            .build();

    protected static final TypeMap<ParticleType> PARTICLE_TYPES = Bedrock_v622.PARTICLE_TYPES.toBuilder()
            .insert(87, ParticleType.DUST_PLUME)
            .insert(88, ParticleType.WHITE_SMOKE)
            .build();

    protected static final TypeMap<LevelEventType> LEVEL_EVENTS = Bedrock_v622.LEVEL_EVENTS.toBuilder()
            .insert(2040, LevelEvent.DUST_PLUME)
            .replace(3609, LevelEvent.PARTICLE_SHOOT_WHITE_SMOKE)
            .insert(3610, LevelEvent.ALL_PLAYERS_SLEEPING)
            .insert(16384, PARTICLE_TYPES)
            .build();

    protected static final EntityDataTypeMap ENTITY_DATA = Bedrock_v622.ENTITY_DATA.toBuilder()
            .update(EntityDataTypes.AREA_EFFECT_CLOUD_PARTICLE, new TypeMapTransformer(PARTICLE_TYPES))
            .build();

    protected static final TypeMap<ContainerSlotType> CONTAINER_SLOT_TYPES = Bedrock_v622.CONTAINER_SLOT_TYPES.toBuilder()
            .insert(62, ContainerSlotType.CRAFTER_BLOCK_CONTAINER)
            .build();

    protected static final TypeMap<SoundEvent> SOUND_EVENTS = Bedrock_v622.SOUND_EVENTS.toBuilder()
            .replace(479, SoundEvent.CRAFTER_CRAFT)
            .insert(480, SoundEvent.CRAFTER_FAILED)
            .insert(481, SoundEvent.DECORATED_POT_INSERT)
            .insert(482, SoundEvent.DECORATED_POT_INSERT_FAILED)
            .insert(483, SoundEvent.CRAFTER_DISABLE_SLOT)
            .insert(490, SoundEvent.COPPER_BULB_ON)
            .insert(491, SoundEvent.COPPER_BULB_OFF)
            .insert(492, SoundEvent.UNDEFINED)
            .build();

    public static final BedrockCodec CODEC = Bedrock_v622.CODEC.toBuilder()
            .raknetProtocolVersion(11)
            .protocolVersion(630)
            .minecraftVersion("1.20.50")
            .helper(() -> new CustomBedrockCodecHelper(ENTITY_DATA, GAME_RULE_TYPES, ITEM_STACK_REQUEST_TYPES, CONTAINER_SLOT_TYPES, PLAYER_ABILITIES, TEXT_PROCESSING_ORIGINS))
            .updateSerializer(LevelEventPacket.class, new LevelEventSerializer_v291(LEVEL_EVENTS))
            .updateSerializer(LevelEventGenericPacket.class, new LevelEventGenericSerializer_v361(LEVEL_EVENTS))
            .updateSerializer(LevelSoundEvent1Packet.class, new LevelSoundEvent1Serializer_v291(SOUND_EVENTS))
            .updateSerializer(LevelSoundEvent2Packet.class, new LevelSoundEvent2Serializer_v313(SOUND_EVENTS))
            .updateSerializer(LevelSoundEventPacket.class, new LevelSoundEventSerializer_v332(SOUND_EVENTS))
            .updateSerializer(ShowStoreOfferPacket.class, ShowStoreOfferSerializer_v630.INSTANCE)
            .registerPacket(ToggleCrafterSlotRequestPacket::new, new ToggleCrafterSlotRequestSerializer_v630(), 306, PacketRecipient.SERVER)
            .registerPacket(SetPlayerInventoryOptionsPacket::new, new SetPlayerInventoryOptionsSerializer_v360(), 307, PacketRecipient.BOTH)
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