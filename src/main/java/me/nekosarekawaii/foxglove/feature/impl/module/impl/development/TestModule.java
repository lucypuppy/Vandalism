package me.nekosarekawaii.foxglove.feature.impl.module.impl.development;

import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import me.nekosarekawaii.foxglove.value.Value;
import me.nekosarekawaii.foxglove.value.ValueCategory;
import me.nekosarekawaii.foxglove.value.values.BooleanValue;
import me.nekosarekawaii.foxglove.value.values.number.IntegerValue;

@ModuleInfo(name = "Test", description = "This is just a module for development purposes.", category = FeatureCategory.DEVELOPMENT, isExperimental = true)
public class TestModule extends Module {

    private final ValueCategory testValueCategory = new ValueCategory("Test", "Test Category", this);

    private final Value<Boolean> booleanValue = new BooleanValue("Test Boolean", "Testa wadawdwad", this.testValueCategory, false);

    private final Value<Integer> integerValue = new IntegerValue("Test Integer", "awdawdawda", this.testValueCategory, 1);

    private final ValueCategory testValueCategory2 = new ValueCategory("Test 2", "Test Category", this.testValueCategory);

    private final ValueCategory testValueCategory3 = new ValueCategory("Test 3", "Test Category", this.testValueCategory2);

}
