package com.enjoylearning.mybatis;

import java.io.IOException;
import java.io.InputStream;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import com.enjoylearning.mybatis.entity.TUser;
import com.enjoylearning.mybatis.mapper.TUserMapper;

public class MybatisQuickStart {

	private SqlSessionFactory sqlSessionFactory;

	@Before
	public void init() throws IOException {
		String resource = "mybatis-config.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		// 1.读取mybatis配置文件创SqlSessionFactory
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		inputStream.close();
	}


    @Test
    // 快速入门
    public void quickStart() throws IOException {
        // 2.获取sqlSession,sqlSessionFactory维护的是连接池
        SqlSession sqlSession = sqlSessionFactory.openSession();
        // 3.获取对应mapper,动态代理生成实现类
        TUserMapper mapper = sqlSession.getMapper(TUserMapper.class);
        // 4.执行查询语句并返回结果
        TUser user = mapper.selectByPrimaryKey(1);
        System.out.println(user.toString());
    }

    //一级缓存
	@Test
	public void oneLevelCache() throws IOException {
		// 2.获取sqlSession,sqlSessionFactory维护的是连接池
		SqlSession sqlSession = sqlSessionFactory.openSession();
		// 3.获取对应mapper,动态代理生成实现类
		TUserMapper mapper = sqlSession.getMapper(TUserMapper.class);
		// 4.执行查询语句并返回结果
		TUser user = mapper.selectByPrimaryKey(1);
		System.out.println(user.toString());
        System.out.println("");
        System.out.println(mapper.selectByPrimaryKey(1).toString());

        sqlSession.close();
        System.out.println();
        SqlSession sqlSession2 = sqlSessionFactory.openSession();
        TUserMapper mapper2 = sqlSession2.getMapper(TUserMapper.class);
        System.out.println(mapper2.selectByPrimaryKey(1).toString());

    }


    //二级缓存（namespace单位的缓存）
    @Test
    public void twoLevelCache() throws IOException {
        // 2.获取sqlSession,sqlSessionFactory维护的是连接池
        SqlSession sqlSession = sqlSessionFactory.openSession();
        // 3.获取对应mapper,动态代理生成实现类
        TUserMapper mapper = sqlSession.getMapper(TUserMapper.class);
        // 4.执行查询语句并返回结果
        TUser user = mapper.selectByPrimaryKey(1);
        System.out.println(user.toString());
        System.out.println("");
        // Cache Hit Ratio 代表二级缓存
        System.out.println(mapper.selectByPrimaryKey(1).toString());

        sqlSession.close();
        System.out.println();

        SqlSession sqlSession2 = sqlSessionFactory.openSession();
        TUserMapper mapper2 = sqlSession2.getMapper(TUserMapper.class);
        //使用了二级缓存
        System.out.println(mapper2.selectByPrimaryKey(1).toString());

        System.out.println();
        System.out.println(mapper2.selectByPrimaryKey(1).toString());

    }
	
}
