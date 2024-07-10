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
import org.cloudburstmc.protocol.bedrock.codec.v575.BedrockCodecHelper_v575;
import org.cloudburstmc.protocol.bedrock.codec.v582.Bedrock_v582;
import org.cloudburstmc.protocol.bedrock.codec.v589.Bedrock_v589;
import org.cloudburstmc.protocol.bedrock.codec.v589.serializer.EmoteSerializer_v589;
import org.cloudburstmc.protocol.bedrock.codec.v589.serializer.EventSerializer_v589;
import org.cloudburstmc.protocol.bedrock.codec.v589.serializer.StartGameSerializer_v589;
import org.cloudburstmc.protocol.bedrock.codec.v589.serializer.UnlockedRecipesSerializer_v589;
import org.cloudburstmc.protocol.bedrock.data.Ability;
import org.cloudburstmc.protocol.bedrock.data.EncodingSettings;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerSlotType;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.TextProcessingEventOrigin;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestActionType;
import org.cloudburstmc.protocol.bedrock.data.skin.*;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.common.util.TypeMap;

import java.util.List;
import java.util.function.Supplier;

public class CustomNetworkSettings589 extends Bedrock_v589 {

    public static final Supplier<EncodingSettings> SETTINGS = () -> EncodingSettings.builder()
            .maxByteArraySize(ProxyServer.getInstance().getConfiguration().getNetworkSettings().maxByteArraySize())
            .maxListSize(ProxyServer.getInstance().getConfiguration().getNetworkSettings().maxListSize())
            .maxNetworkNBTSize(ProxyServer.getInstance().getConfiguration().getNetworkSettings().maxNetworkNBTSize())
            .maxItemNBTSize(ProxyServer.getInstance().getConfiguration().getNetworkSettings().maxItemNBTSize())
            .maxStringLength(ProxyServer.getInstance().getConfiguration().getNetworkSettings().maxStringLength())
            .build();

    protected static final TypeMap<SoundEvent> SOUND_EVENTS = Bedrock_v582.SOUND_EVENTS.toBuilder()
            .replace(466, SoundEvent.SNIFFER_EGG_CRACK)
            .insert(467, SoundEvent.SNIFFER_EGG_HATCHED)
            .insert(468, SoundEvent.WAXED_SIGN_INTERACT_FAIL)
            .insert(469, SoundEvent.RECORD_RELIC)
            .insert(470, SoundEvent.UNDEFINED)
            .build();

    protected static final TypeMap<TextProcessingEventOrigin> TEXT_PROCESSING_ORIGINS = TypeMap.builder(TextProcessingEventOrigin.class)
            .insert(0, TextProcessingEventOrigin.SERVER_CHAT_PUBLIC)
            .insert(1, TextProcessingEventOrigin.SERVER_CHAT_WHISPER)
            .insert(2, TextProcessingEventOrigin.SIGN_TEXT)
            .insert(3, TextProcessingEventOrigin.ANVIL_TEXT)
            .insert(4, TextProcessingEventOrigin.BOOK_AND_QUILL_TEXT)
            .insert(5, TextProcessingEventOrigin.COMMAND_BLOCK_TEXT)
            .insert(6, TextProcessingEventOrigin.BLOCK_ENTITY_DATA_TEXT)
            .insert(7, TextProcessingEventOrigin.JOIN_EVENT_TEXT)
            .insert(8, TextProcessingEventOrigin.LEAVE_EVENT_TEXT)
            .insert(9, TextProcessingEventOrigin.SLASH_COMMAND_TEXT)
            .insert(10, TextProcessingEventOrigin.CARTOGRAPHY_TEXT)
            .insert(11, TextProcessingEventOrigin.KICK_COMMAND)
            .insert(12, TextProcessingEventOrigin.TITLE_COMMAND)
            .insert(13, TextProcessingEventOrigin.SUMMON_COMMAND)
            .build();

    public static final BedrockCodec CODEC = Bedrock_v582.CODEC.toBuilder()
            .raknetProtocolVersion(11)
            .protocolVersion(589)
            .minecraftVersion("1.20.0")
            .helper(() -> new CustomBedrockCodecHelper(ENTITY_DATA, GAME_RULE_TYPES, ITEM_STACK_REQUEST_TYPES, CONTAINER_SLOT_TYPES, PLAYER_ABILITIES, TEXT_PROCESSING_ORIGINS))
            .updateSerializer(StartGamePacket.class, new StartGameSerializer_v589())
            .updateSerializer(EventPacket.class, EventSerializer_v589.INSTANCE)
            .updateSerializer(EmotePacket.class, EmoteSerializer_v589.INSTANCE)
            .updateSerializer(UnlockedRecipesPacket.class, new UnlockedRecipesSerializer_v589())
            .updateSerializer(LevelSoundEvent1Packet.class, new LevelSoundEvent1Serializer_v291(SOUND_EVENTS))
            .updateSerializer(LevelSoundEvent2Packet.class, new LevelSoundEvent2Serializer_v313(SOUND_EVENTS))
            .updateSerializer(LevelSoundEventPacket.class, new LevelSoundEventSerializer_v332(SOUND_EVENTS))
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