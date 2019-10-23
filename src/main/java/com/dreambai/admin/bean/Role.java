package com.dreambai.admin.bean;

import lombok.Data;

/**
 * Created by dreambai on 2017/12/17.
 */

@Data
public class Role {
	private Long id;
	private String name;

	public Role() {
	}

	public Role(Long id, String name) {
		this.id = id;
		this.name = name;
	}
}
