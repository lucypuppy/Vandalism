package de.evilcodez.supermod.render.gl.render;

import de.evilcodez.supermod.render.gl.render.passes.RenderPass;

public interface AttribConsumerProvider {

    AttribConsumerSet getAttribConsumers(RenderPass pass);
}
