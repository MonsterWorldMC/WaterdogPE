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
import org.cloudburstmc.protocol.bedrock.codec.v575.BedrockCodecHelper_v575;
import org.cloudburstmc.protocol.bedrock.codec.v575.Bedrock_v575;
import org.cloudburstmc.protocol.bedrock.codec.v582.Bedrock_v582;
import org.cloudburstmc.protocol.bedrock.codec.v589.Bedrock_v589;
import org.cloudburstmc.protocol.bedrock.codec.v594.Bedrock_v594;
import org.cloudburstmc.protocol.bedrock.codec.v594.serializer.AgentAnimationSerializer_v594;
import org.cloudburstmc.protocol.bedrock.codec.v594.serializer.AvailableCommandsSerializer_v594;
import org.cloudburstmc.protocol.bedrock.data.Ability;
import org.cloudburstmc.protocol.bedrock.data.EncodingSettings;
import org.cloudburstmc.protocol.bedrock.data.PacketRecipient;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParam;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityDataFormat;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityDataTypes;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityFlag;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerSlotType;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.TextProcessingEventOrigin;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestActionType;
import org.cloudburstmc.protocol.bedrock.data.skin.*;
import org.cloudburstmc.protocol.bedrock.packet.AgentAnimationPacket;
import org.cloudburstmc.protocol.bedrock.packet.AvailableCommandsPacket;
import org.cloudburstmc.protocol.bedrock.packet.ScriptCustomEventPacket;
import org.cloudburstmc.protocol.bedrock.transformer.FlagTransformer;
import org.cloudburstmc.protocol.common.util.TypeMap;

import java.util.List;
import java.util.function.Supplier;

public class CustomNetworkSettings594 extends Bedrock_v594 {

    public static final Supplier<EncodingSettings> SETTINGS = () -> EncodingSettings.builder()
            .maxByteArraySize(ProxyServer.getInstance().getConfiguration().getNetworkSettings().maxByteArraySize())
            .maxListSize(ProxyServer.getInstance().getConfiguration().getNetworkSettings().maxListSize())
            .maxNetworkNBTSize(ProxyServer.getInstance().getConfiguration().getNetworkSettings().maxNetworkNBTSize())
            .maxItemNBTSize(ProxyServer.getInstance().getConfiguration().getNetworkSettings().maxItemNBTSize())
            .maxStringLength(ProxyServer.getInstance().getConfiguration().getNetworkSettings().maxStringLength())
            .build();

    protected static final TypeMap<EntityFlag> ENTITY_FLAGS = Bedrock_v575.ENTITY_FLAGS.toBuilder()
            .insert(114, EntityFlag.CRAWLING)
            .build();

    protected static final EntityDataTypeMap ENTITY_DATA = Bedrock_v589.ENTITY_DATA.toBuilder()
            .insert(EntityDataTypes.COLLISION_BOX, 130, EntityDataFormat.VECTOR3F)
            .update(EntityDataTypes.FLAGS, new FlagTransformer(ENTITY_FLAGS, 0))
            .update(EntityDataTypes.FLAGS_2, new FlagTransformer(ENTITY_FLAGS, 1))
            .build();

    protected static final TypeMap<CommandParam> COMMAND_PARAMS = Bedrock_v582.COMMAND_PARAMS.toBuilder()
            .insert(134217728, CommandParam.CHAINED_COMMAND)
            .build();

    public static final BedrockCodec CODEC = Bedrock_v589.CODEC.toBuilder()
            .raknetProtocolVersion(11)
            .protocolVersion(594)
            .minecraftVersion("1.20.10")
            .helper(() -> new CustomBedrockCodecHelper(ENTITY_DATA, GAME_RULE_TYPES, ITEM_STACK_REQUEST_TYPES, CONTAINER_SLOT_TYPES, PLAYER_ABILITIES, TEXT_PROCESSING_ORIGINS))
            .deregisterPacket(ScriptCustomEventPacket.class)
            .updateSerializer(AvailableCommandsPacket.class, new AvailableCommandsSerializer_v594(COMMAND_PARAMS))
            .registerPacket(AgentAnimationPacket::new, new AgentAnimationSerializer_v594(), 304, PacketRecipient.CLIENT)
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