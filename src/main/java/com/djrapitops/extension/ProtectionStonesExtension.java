/*
    Copyright(c) 2021 AuroraLS3

    The MIT License(MIT)

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files(the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions :
    The above copyright notice and this permission notice shall be included in
    all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
    THE SOFTWARE.
*/
package com.djrapitops.extension;

import com.djrapitops.plan.extension.DataExtension;
import com.djrapitops.plan.extension.ElementOrder;
import com.djrapitops.plan.extension.annotation.DataBuilderProvider;
import com.djrapitops.plan.extension.annotation.PluginInfo;
import com.djrapitops.plan.extension.annotation.TabInfo;
import com.djrapitops.plan.extension.builder.ExtensionDataBuilder;
import com.djrapitops.plan.extension.icon.Color;
import com.djrapitops.plan.extension.icon.Family;
import com.djrapitops.plan.extension.icon.Icon;
import com.djrapitops.plan.extension.table.Table;
import dev.espi.protectionstones.PSPlayer;
import dev.espi.protectionstones.PSRegion;
import dev.espi.protectionstones.utils.WGUtils;
import org.bukkit.World;

import java.util.*;
import java.util.stream.Collectors;

/**
 * DataExtension.
 *
 * @author AuroraLS3
 */
@PluginInfo(name = "ProtectionStones", iconFamily = Family.SOLID, color = Color.AMBER) // cube
@TabInfo(tab = "Owned Regions", iconName = "square", iconFamily = Family.REGULAR, elementOrder = {ElementOrder.VALUES, ElementOrder.TABLE})
@TabInfo(tab = "Member Regions", iconName = "square", iconFamily = Family.REGULAR, elementOrder = {ElementOrder.VALUES, ElementOrder.TABLE})
@TabInfo(tab = "Homes", iconName = "home", elementOrder = {ElementOrder.VALUES, ElementOrder.TABLE})
public class ProtectionStonesExtension implements DataExtension {

    public ProtectionStonesExtension() { }

    @DataBuilderProvider
    public ExtensionDataBuilder playerData(UUID playerUUID) {
        PSPlayer player = PSPlayer.fromUUID(playerUUID);

        Set<World> worlds = WGUtils.getAllRegionManagers().keySet();

        List<PSRegion> regions = worlds.stream()
                .map(world -> player.getPSRegions(world, true))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        List<PSRegion> ownedRegions = new ArrayList<>();
        List<PSRegion> memberInRegions = new ArrayList<>();
        for (PSRegion region : regions) {
            if (region.isOwner(playerUUID)) {
                ownedRegions.add(region);
            } else {
                memberInRegions.add(region);
            }
        }

        List<PSRegion> homes = worlds.stream()
                .map(player::getHomes)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        return newExtensionDataBuilder()
                .addTable("owned-regions", createRegionTable(ownedRegions), Color.GREEN, "Owned Regions")
                .addTable("member-regions", createRegionTable(memberInRegions), Color.BLUE, "Member Regions")
                .addTable("homes", createRegionTable(homes), Color.LIGHT_GREEN, "Homes");
    }

    private Table createRegionTable(List<PSRegion> regions) {
        Table.Factory regionTable = Table.builder()
                .columnOne("Region", Icon.called("square").of(Family.REGULAR).build())
                .columnTwo("Type", Icon.called("cube").build())
                .columnThree("Tax", Icon.called("money-bill-wave").build());
        for (PSRegion region : regions) {
            regionTable.addRow(
                    region.getName(),
                    region.getType(),
                    region.getTaxRate()
            );
        }
        return regionTable.build();
    }
}