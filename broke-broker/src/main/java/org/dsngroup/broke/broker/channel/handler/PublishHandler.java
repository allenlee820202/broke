/*
 * Copyright (c) 2017 original authors and authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dsngroup.broke.broker.channel.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import org.dsngroup.broke.broker.storage.InMemoryPool;
import org.dsngroup.broke.protocol.*;

/**
 * This ReadDropHandler is the example class from netty userguide.
 * Use this for testing the channel pipeline and server.
 */
public class PublishHandler extends ChannelInboundHandlerAdapter {

    /**
     * Read the message from channel, parse and publish to {@link InMemoryPool}
     * @param ctx {@see ChannelHandlerContext}
     * @param msg The message of the channel read.
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf incoming = (ByteBuf) msg;
        StringBuilder packet = new StringBuilder();
        try {
            while (incoming.isReadable()) {
                packet.append((char) incoming.readByte());
            }
            // TODO: Dealing with complex protocol parsing
            Message newMessage = MessageBuilder.build(packet.toString());
            if (newMessage.getMethod() == Method.PUBLISH) {
                // TODO: write the payload to InMemoryPool
                // TODO: Send PUBACK message to the client
                PublishMessage newPublishMessage = (PublishMessage) newMessage;
                // TODO: We'll log System.out and System.err in the future
                System.out.println("New message published to topic: "+newPublishMessage.getTopic()+
                        "with payload: "+newPublishMessage.getPayload());
                // System.out.println(InMemoryPool.getContentFromTopic(newMessage.getTopic()));
            } else if (newMessage.getMethod() == Method.CONNECT) {
                // TODO: Send CONNACK message to the client.
                ConnectMessage newConnectMessage = (ConnectMessage) newMessage;
                // InMemoryPool.putContentOnTopic(newMessage.getTopic(), newMessage.getPayload());
                System.out.println("New connection with QoS:" + newConnectMessage.getQos()
                        + " and critical option: "+newConnectMessage.getCriticalOption());

            }
        } finally {
            // The msg object is an reference counting object.
            ReferenceCountUtil.release(msg);
        }
    }

    /**
     * Catch exception, and close connections.
     * @param ctx {@see ChannelHandlerContext}
     * @param cause rethrow
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        // TODO: log this, instead of printStackTrace()
        cause.printStackTrace();
        ctx.close();
    }
}
