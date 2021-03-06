package com.bitdubai.fermat_dmp_plugin.layer.world.coinapult.developer.bitdubai.version_1.coinapult_http_client;

import java.sql.Timestamp;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public class AccountNew {
	public static class Json extends GenericJson {
		@Key
		public String success;

		@Key
		public long when;

		public Timestamp getWhen() {
			return new Timestamp(when * 1000);
		}
	}

	public static class JsonNew extends Json {
		@Key
		public String terms;

		@Key
		public String info;
	}
}
