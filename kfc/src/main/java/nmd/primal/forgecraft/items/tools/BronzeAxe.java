package nmd.primal.forgecraft.items.tools;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nmd.primal.core.common.helper.PlayerHelper;
import nmd.primal.forgecraft.ModInfo;
import nmd.primal.forgecraft.init.ModItems;
import nmd.primal.forgecraft.util.ToolNBT;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by mminaie on 6/25/17.
 */
public class BronzeAxe extends ItemAxe implements ToolNBT {

    public BronzeAxe(String name, Item.ToolMaterial material) {
        super(material, 5, -2f);
        this.setUnlocalizedName(name);
        this.setRegistryName(name);
        this.setCreativeTab(ModInfo.TAB_FORGECRAFT);
        this.setMaxStackSize(1);
        this.setNoRepair();

        this.addPropertyOverride(new ResourceLocation("type"), new IItemPropertyGetter() {

            /***

             hot . emerald . diamond . redstone . lapis
             0  .    0    .    0    .     0    .   0

             ***/

            @SideOnly(Side.CLIENT)
            public float apply(ItemStack item, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
            {
                if (item.hasTagCompound()) {


                    if (getModifiers(item) != 0) {
                        if (getEmerald(item)) {
                            return 0.1F;
                        }

                        if (getDiamondLevel(item) == 1) {
                            return 0.2F;
                        }

                        if ( getRedstoneLevel(item) == 1) {
                            return 0.3F;
                        }

                        if (getLapisLevel(item) == 1) {
                            return 0.4F;
                        }
                    }
                    if (getModifiers(item) == 0) {
                        return 0.0F;
                    }
                }
                return 0.0F;
            }
        });
    }

    public static boolean isHidden()
    {
        return false;
    }

    @Override
    public void onCreated(ItemStack item, World world, EntityPlayer playerIn) {

        if(!world.isRemote) {
            if (!item.hasTagCompound()) {
                item.setTagCompound(new NBTTagCompound());
                NBTTagCompound tags = new NBTTagCompound();

                item.getTagCompound().setTag("tags", tags);

                /*setHot(item, false);

                setHot(item, false);
                setEmerald(item, false);
                setDiamondLevel(item, 0);
                setRedstoneLevel(item, 0);
                setLapisLevel(item, 0);
                setModifiers(item, 0);*/
            }
        }

    }

    @Override
    public void onUpdate(ItemStack item, World world, Entity player, int itemSlot, boolean isSelected) {
        if(!world.isRemote) {
            if (!item.hasTagCompound()) {
                item.setTagCompound(new NBTTagCompound());
                NBTTagCompound tags = new NBTTagCompound();

                item.getTagCompound().setTag("tags", tags);

                setHot(item, false);
                setEmerald(item, false);
                setDiamondLevel(item, 0);
                setRedstoneLevel(item, 0);
                setLapisLevel(item, 0);
                setModifiers(item, 0);
            }
            if( this.getMaxDamage(item) - this.getDamage(item) <= 1 ){
                PlayerHelper.spawnItemOnPlayer(world, (EntityPlayer) player, new ItemStack(ModItems.bronzeaxehead, 1));
            }
        }
    }

    //public void onItemTooltip(ItemTooltipEvent event){

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack item, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
        if(!item.isEmpty()) {
            if (player.getEntityWorld().isRemote) {

                if (item.hasTagCompound()) {

                    tooltip.add(ChatFormatting.GRAY + "Upgrades Left: " + (1 - getModifiers(item)) );
                    if (getEmerald(item) == true) {
                        tooltip.add(ChatFormatting.DARK_GREEN + "Emerald");
                    }
                    if (getDiamondLevel(item) > 0) {
                        tooltip.add(ChatFormatting.AQUA + "Diamond Level: " + getDiamondLevel(item));
                    }
                    if (getRedstoneLevel(item) > 0) {
                        tooltip.add(ChatFormatting.RED + "Redstone Level: " + "1" );
                    }
                    if (getLapisLevel(item) > 0) {
                        tooltip.add(ChatFormatting.BLUE + "Lapis Level: " + "5" );
                    }
                    tooltip.add(ChatFormatting.LIGHT_PURPLE + "Damage: " + item.getItemDamage() );
                }
            }
        }
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player)
    {
        if(!player.world.isRemote){
            World world = player.getEntityWorld();
            //System.out.println(world.getBlockState(pos).getBlock());
            if(itemstack.getItem() instanceof CustomAxe){
                if( getEmerald(itemstack)){
                    itemstack.addEnchantment(Enchantment.getEnchantmentByID(33), 1);
                }
                /*if( getDiamondLevel(itemstack) > 0 ){
                    itemstack.getItem().setHarvestLevel("pickaxe", 3);
                }*/
                if ( getLapisLevel(itemstack) > 0) {
                    itemstack.addEnchantment(Enchantment.getEnchantmentByID(35), 5);
                }
            }
        }

        return false;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker)
    {
        if(stack.getMaxDamage() - stack.getItemDamage() >1 ) {
            stack.damageItem(1, attacker);
            return true;
        } else {
            ItemStack dropStack = new ItemStack(ModItems.brokenbronzetool, 1);
            EntityPlayer player = (EntityPlayer) attacker;
            World world = attacker.getEntityWorld();
            PlayerHelper.spawnItemOnPlayer(world, player, dropStack);
            attacker.renderBrokenItemStack(stack);
            stack.shrink(1);
            return true;
        }
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase entityLiving)
    {
        if (!world.isRemote && (double)state.getBlockHardness(world, pos) != 0.0D)
        {
            if(stack.getMaxDamage() - stack.getItemDamage() >1 ) {
                stack.getTagCompound().removeTag("ench");
                if(getDiamondLevel(stack) > 0) {
                    if(ThreadLocalRandom.current().nextInt(0, getDiamondLevel(stack)) == 0) {
                        stack.damageItem(1, entityLiving);
                    }
                } else stack.damageItem(1, entityLiving);
            } else {
                ItemStack dropStack = new ItemStack(ModItems.brokenbronzetool, 1);
                EntityPlayer player = (EntityPlayer) entityLiving;
                PlayerHelper.spawnItemOnPlayer(world, player, dropStack);
                entityLiving.renderBrokenItemStack(stack);
                stack.shrink(1);
            }
        }

        return true;
    }

    @Override
    public float getStrVsBlock(ItemStack stack, IBlockState state)
    {
        Material material = state.getMaterial();
        //return material != Material.IRON && material != Material.ANVIL && material != Material.ROCK ? super.getStrVsBlock(stack, state) : this.efficiencyOnProperMaterial;

        if(material != Material.WOOD && material != Material.PLANTS && material != Material.VINE){
            return  super.getStrVsBlock(stack, state);
        } else {
            return this.efficiencyOnProperMaterial + 1;
        }

    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack)
    {
        return false;
    }

    @Override
    public boolean isRepairable()
    {
        return false;
    }

    public int getItemEnchantability(ItemStack stack)
    {
        return 0;
    }

}
