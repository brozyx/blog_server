package com.dreambai.admin.service;

import com.dreambai.admin.bean.Role;
import com.dreambai.admin.bean.User;
import com.dreambai.utils.Util;
import com.dreambai.admin.mapper.RolesMapper;
import com.dreambai.admin.mapper.UserMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by dreambai on 2017/12/17.
 */
@Service
@Transactional
public class UserService implements UserDetailsService {
	@Resource
	UserMapper userMapper;
	@Resource
	RolesMapper rolesMapper;

	@Override
	public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
		User user = userMapper.loadUserByUsername(s);
		if (user == null) {
			//避免返回null，这里返回一个不含有任何值的User对象，在后期的密码比对过程中一样会验证失败
			return new User();
		}
		//查询用户的角色信息，并返回存入user中
		List<Role> roles = rolesMapper.getRolesByUid(user.getId());
		user.setRoles(roles);
		return user;
	}

	/**
	 * @param user
	 * @return 0表示成功
	 * 1表示用户名重复
	 * 2表示失败
	 */
	public int reg(User user) {
		User loadUserByUsername = userMapper.loadUserByUsername(user.getUsername());
		if (loadUserByUsername != null) {
			return 1;
		}
		//插入用户,插入之前先对密码进行加密
		user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
		user.setEnabled(true);//用户可用
		long result = userMapper.reg(user);
		//配置用户的角色，默认都是普通用户
		String[] roles = new String[]{"2"};
		int i = rolesMapper.addRoles(roles, user.getId());
		boolean b = i == roles.length && result == 1;
		if (b) {
			return 0;
		} else {
			return 2;
		}
	}

	public int updateUserEmail(String email) {
		return userMapper.updateUserEmail(email, Util.getCurrentUser().getId());
	}

	public List<User> getUserByNickname(String nickname) {
		List<User> list = userMapper.getUserByNickname(nickname);
		return list;
	}

	public List<Role> getAllRole() {
		return userMapper.getAllRole();
	}

	public int updateUserEnabled(Boolean enabled, Long uid) {
		return userMapper.updateUserEnabled(enabled, uid);
	}

	public int deleteUserById(Long uid) {
		return userMapper.deleteUserById(uid);
	}

	public int updateUserRoles(Long[] rids, Long id) {
		int i = userMapper.deleteUserRolesByUid(id);
		return userMapper.setUserRoles(rids, id);
	}

	public User getUserById(Long id) {
		return userMapper.getUserById(id);
	}
}
