/*
 * (C) Copyright 2018 Siyue Holding Group.
 */
package com.siyueli.platform.service.member.server.config.wechat;

import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import redis.clients.jedis.JedisCluster;


/**
 * 基于Redis的微信配置provider
 *
 */
public class WxMpOnRedisConfigStorage extends WxMpInMemoryConfigStorage {

  private final static String ACCESS_TOKEN_KEY = "wechat_access_token_";

  private final static String JSAPI_TICKET_KEY = "wechat_jsapi_ticket_";

  private final static String CARDAPI_TICKET_KEY = "wechat_cardapi_ticket_";

  private String accessTokenKey;

  private String jsapiTicketKey;

  private String cardapiTicketKey;

  protected final JedisCluster jedisCluster;

  public WxMpOnRedisConfigStorage(JedisCluster jedisCluster) {
    this.jedisCluster = jedisCluster;
  }

  /**
   * 每个公众号生成独有的存储key
   */
  @Override
  public void setAppId(String appId) {
    super.setAppId(appId);
    this.accessTokenKey = ACCESS_TOKEN_KEY.concat(appId);
    this.jsapiTicketKey = JSAPI_TICKET_KEY.concat(appId);
    this.cardapiTicketKey = CARDAPI_TICKET_KEY.concat(appId);
  }

  @Override
  public String getAccessToken() {

    return jedisCluster.get(this.accessTokenKey);
  }

  @Override
  public boolean isAccessTokenExpired() {

    return jedisCluster.ttl(accessTokenKey) < 2;
  }

  @Override
  public synchronized void updateAccessToken(String accessToken, int expiresInSeconds) {

    jedisCluster.setex(this.accessTokenKey, expiresInSeconds - 200, accessToken);
  }

  @Override
  public void expireAccessToken() {

    jedisCluster.expire(this.accessTokenKey, 0);
  }

  @Override
  public String getJsapiTicket() {

    return jedisCluster.get(this.jsapiTicketKey);
  }

  @Override
  public boolean isJsapiTicketExpired() {

    return jedisCluster.ttl(this.jsapiTicketKey) < 2;
  }

  @Override
  public synchronized void updateJsapiTicket(String jsapiTicket, int expiresInSeconds) {

    jedisCluster.setex(this.jsapiTicketKey, expiresInSeconds - 200, jsapiTicket);
  }

  @Override
  public void expireJsapiTicket() {

    jedisCluster.expire(this.jsapiTicketKey, 0);
  }

  @Override
  public String getCardApiTicket() {

    return jedisCluster.get(this.cardapiTicketKey);
  }

  @Override
  public boolean isCardApiTicketExpired() {

    return jedisCluster.ttl(this.cardapiTicketKey) < 2;
  }

  @Override
  public synchronized void updateCardApiTicket(String cardApiTicket, int expiresInSeconds) {
    jedisCluster.setex(this.cardapiTicketKey, expiresInSeconds - 200, cardApiTicket);
  }

  @Override
  public void expireCardApiTicket() {

    jedisCluster.expire(this.cardapiTicketKey, 0);
  }
}
