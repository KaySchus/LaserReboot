package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.VariantList;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Selector
{
    private final ICondition condition;
    private final VariantList variantList;

    public Selector(ICondition p_i46562_1_, VariantList p_i46562_2_)
    {
        if (p_i46562_1_ == null)
        {
            throw new IllegalArgumentException("Missing condition for selector");
        }
        else if (p_i46562_2_ == null)
        {
            throw new IllegalArgumentException("Missing variant for selector");
        }
        else
        {
            this.condition = p_i46562_1_;
            this.variantList = p_i46562_2_;
        }
    }

    public VariantList getVariantList()
    {
        return this.variantList;
    }

    public Predicate<IBlockState> getPredicate(BlockStateContainer p_188166_1_)
    {
        return this.condition.getPredicate(p_188166_1_);
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else
        {
            if (p_equals_1_ instanceof Selector)
            {
                Selector selector = (Selector)p_equals_1_;

                if (this.condition.equals(selector.condition))
                {
                    return this.variantList.equals(selector.variantList);
                }
            }

            return false;
        }
    }

    public int hashCode()
    {
        return 31 * this.condition.hashCode() + this.variantList.hashCode();
    }

    @SideOnly(Side.CLIENT)
    public static class Deserializer implements JsonDeserializer<Selector>
        {
            private static final Function<JsonElement, ICondition> FUNCTION_OR_AND = new Function<JsonElement, ICondition>()
            {
                public ICondition apply(JsonElement p_apply_1_)
                {
                    return p_apply_1_ == null ? null : Selector.Deserializer.getOrAndCondition(p_apply_1_.getAsJsonObject());
                }
            };
            private static final Function<Entry<String, JsonElement>, ICondition> FUNCTION_PROPERTY_VALUE = new Function<Entry<String, JsonElement>, ICondition>()
            {
                public ICondition apply(Entry<String, JsonElement> p_apply_1_)
                {
                    return p_apply_1_ == null ? null : Selector.Deserializer.makePropertyValue(p_apply_1_);
                }
            };

            public Selector deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
            {
                JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
                return new Selector(this.getWhenCondition(jsonobject), (VariantList)p_deserialize_3_.deserialize(jsonobject.get("apply"), VariantList.class));
            }

            private ICondition getWhenCondition(JsonObject p_188159_1_)
            {
                return p_188159_1_.has("when") ? getOrAndCondition(JsonUtils.getJsonObject(p_188159_1_, "when")) : ICondition.TRUE;
            }

            static ICondition getOrAndCondition(JsonObject p_188158_0_)
            {
                Set<Entry<String, JsonElement>> set = p_188158_0_.entrySet();

                if (set.isEmpty())
                {
                    throw new JsonParseException("No elements found in selector");
                }
                else
                {
                    return (ICondition)(set.size() == 1 ? (p_188158_0_.has("OR") ? new ConditionOr(Iterables.transform(JsonUtils.getJsonArray(p_188158_0_, "OR"), FUNCTION_OR_AND)) : (p_188158_0_.has("AND") ? new ConditionAnd(Iterables.transform(JsonUtils.getJsonArray(p_188158_0_, "AND"), FUNCTION_OR_AND)) : makePropertyValue((Entry)set.iterator().next()))) : new ConditionAnd(Iterables.transform(set, FUNCTION_PROPERTY_VALUE)));
                }
            }

            private static ConditionPropertyValue makePropertyValue(Entry<String, JsonElement> p_188161_0_)
            {
                return new ConditionPropertyValue((String)p_188161_0_.getKey(), ((JsonElement)p_188161_0_.getValue()).getAsString());
            }
        }
}