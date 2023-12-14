package de.nekosarekawaii.vandalism.base.value;

public interface ValueChangeConsumer<OV, NV> {

    NV getNewValue(final OV ov, final NV nv);

}
