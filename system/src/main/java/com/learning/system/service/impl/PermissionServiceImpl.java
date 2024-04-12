package com.learning.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.learning.core.utils.CollectionUtils;
import com.learning.core.utils.CommonBeanUtil;
import com.learning.system.model.dto.PermissionDto;
import com.learning.system.model.dto.RolePermissionDto;
import com.learning.system.model.entity.PermissionEntity;
import com.learning.system.model.entity.RolePermissionEntity;
import com.learning.system.mapper.PermissionMapper;
import com.learning.system.service.PermissionService;
import com.learning.system.service.RolePermissionService;
import com.learning.web.model.param.PageParam;
import com.learning.web.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl
        extends BaseServiceImpl<PermissionMapper, PermissionEntity, PermissionDto>
        implements PermissionService {

    @Resource
    private RolePermissionService rolePermissionService;

    @Override
    public IPage<PermissionDto> pageTree(PageParam page)  {
        // 1. 获取分页查询插件类
        Page<PermissionEntity> iPage = this.getPage(page);
        // 2. 获取查询参数
        Wrapper<PermissionEntity> wrapper = this.getWrapper(page);
        // 3. 分页查询根菜单
        if (wrapper instanceof QueryWrapper) {
            ((QueryWrapper)wrapper).eq("pid", 0);
        }

        IPage<PermissionEntity> permissionPage = this.baseMapper.selectPage(iPage, wrapper);
        // 4.获取对应子菜单
        List<PermissionEntity> records = permissionPage.getRecords();
        List<PermissionDto> dtoList = CommonBeanUtil.copyList(records, PermissionDto.class);

        dtoList.forEach(permission -> {
            List<PermissionEntity> permissionEntities = this.baseMapper.selectList(new QueryWrapper<PermissionEntity>().lambda()
                    .eq(PermissionEntity::getPid, permission.getId()));
            List<PermissionDto> children = CommonBeanUtil.copyList(permissionEntities, PermissionDto.class);

            // 5.设置孙子菜单
            children.forEach(permissionEntity -> {
                permissionEntity.setChildren(CommonBeanUtil.copyList(this.baseMapper.selectList(new QueryWrapper<PermissionEntity>().lambda()
                        .eq(PermissionEntity::getPid, permissionEntity.getId())), PermissionDto.class));
            });


            permission.setChildren(children);
        });

        //6.设置对应子菜单
        Page<PermissionDto> dtoPage = new Page<>();
        return dtoPage.setCurrent(iPage.getCurrent()).setRecords(dtoList).setTotal(iPage.getTotal()).setSize(iPage.getSize());
    }

    @Override
    public List<PermissionDto> getMenuTree() {
        // 1. 获取所有菜单
        LambdaQueryWrapper<PermissionEntity> qw = new QueryWrapper<PermissionEntity>().lambda()
                .ne(PermissionEntity::getPermissionsType, 2)
                .orderByAsc(PermissionEntity::getSort);

        List<PermissionEntity> permissionEntities = this.baseMapper.selectList(qw);

        //2. 生成树
        if(! CollectionUtils.isEmpty(permissionEntities)) {
            return generateTree(permissionEntities);
        }

        return new ArrayList<>();
    }

    @Override
    public Map<String,List<PermissionDto>> getPermissionTree(Long roleId) {
        //1.获取角色绑定的权限值
        List<PermissionDto> menuTreeWithRole = getMenuTreeByRoleId(roleId);
        //2.获取角色未绑定的权限值
        List<PermissionDto> menuTreeWithoutRole = getMenuTreeWithoutRoleCode(roleId);
        //3.设置角色已拥有权限和未拥有权限
        Map<String,List<PermissionDto>> result = new HashMap<>();
        result.put("menuTreeWithRole", menuTreeWithRole);
        result.put("menuTreeWithoutRole", menuTreeWithoutRole);
        return result;
    }

    @Override
    @Transactional(
            rollbackFor = {Exception.class}
    )
    public void insertPermissionRoleBatch(RolePermissionDto rolePermission) {

        //1.删除原有绑定关系
        Long roleId = rolePermission.getRoleId();
        rolePermissionService.getBaseMapper().delete(new QueryWrapper<RolePermissionEntity>().lambda()
                .eq(RolePermissionEntity::getRoleId, roleId));

        //2.获取所有权限对象列表
        List<Long> permissionList = rolePermission.getPermissionIdList();

        if (CollectionUtils.isEmpty(permissionList)) {
            return;
        }

        //3. 创建绑定关系对象
        List<RolePermissionEntity> addList = new ArrayList<>();
        permissionList.forEach(id -> {
            RolePermissionEntity rolePermissionEntity = new RolePermissionEntity();
            rolePermissionEntity.setRoleId(roleId);
            rolePermissionEntity.setPermissionId(id);
            addList.add(rolePermissionEntity);
        });

        //4.添加绑定关系
        rolePermissionService.saveBatch(addList);
    }

    @Override
    public List<PermissionDto> listByPermissionsType(Integer permissionType) {

        List<PermissionDto> permissionList = new ArrayList<>();
        // 1.新增未选择时返回空
        if(permissionType == null) {
            return permissionList;
        }
        // 2.为菜单时直接返回空节点
        if (permissionType == 0) {
            PermissionDto permission = new PermissionDto();
            permission.setId(0L);
            permission.setName("根菜单");
            permissionList.add(permission);
        }
        // 3.其余情况获取父权限
        else {
            List<PermissionEntity> permissionEntityList = this.baseMapper.selectList(new QueryWrapper<PermissionEntity>().lambda().eq(PermissionEntity::getPermissionsType, permissionType - 1));
            permissionList = CommonBeanUtil.copyList(permissionEntityList, PermissionDto.class);
        }

        return permissionList;
    }

    /**
     * 递归获取权限树中所有id列表
     * @param permissionList 权限树数组
     * @return
     */
    private List<PermissionDto> getPermissionIdList(List<PermissionDto> permissionList) {
        List<PermissionDto> idList = new ArrayList<>();

        if(! CollectionUtils.isEmpty(permissionList)) {
            //循环获取列表所有权限id
            for (PermissionDto permissionDto : permissionList) {
                //获取本级权限id
                idList.add(permissionDto);
                //递归获取子权限所有id
                if (! CollectionUtils.isEmpty(permissionDto.getChildren())){
                    idList.addAll(getPermissionIdList(permissionDto.getChildren()));
                }
            }
        }

        return idList;
    }

    private List<PermissionDto> getMenuTreeByRoleId(Long roleId) {
        List<PermissionEntity> permissionEntities = this.baseMapper.getMenuTreeByRoleCode(roleId);

        //2. 生成树
        if(! CollectionUtils.isEmpty(permissionEntities)) {
            return generateTree(permissionEntities);
        }

        return new ArrayList<>();
    }

    private List<PermissionDto> getMenuTreeWithoutRoleCode(Long roleId) {
        //1.指定元素相关对象
        List<RolePermissionEntity> rolePermissionEntities = rolePermissionService.list(new QueryWrapper<RolePermissionEntity>().lambda()
                .eq(RolePermissionEntity::getRoleId, roleId));
        List<PermissionEntity> permissionEntities;
        List<PermissionEntity> allPermissions = this.list();

        //2.未绑定任何对象时 获取全部权限列表
        if(CollectionUtils.isEmpty(rolePermissionEntities)) {
            permissionEntities = allPermissions;
        }
        //3.排除已绑定的权限对象
        else {
            List<Long> idList = rolePermissionEntities.stream().map(RolePermissionEntity::getPermissionId).collect(Collectors.toList());
            Map<Long, List<PermissionEntity>> allMap = allPermissions.stream().collect(Collectors.groupingBy(PermissionEntity::getId));
            //补足没有根节点的权限对象
            permissionEntities = this.list(new QueryWrapper<PermissionEntity>().lambda().notIn(PermissionEntity::getId, idList));
            List<Long> selectId = permissionEntities.stream().map(PermissionEntity::getId).collect(Collectors.toList());

            for (Long id : selectId) {
                PermissionEntity permissionEntity = allMap.get(id).get(0);

                while(permissionEntity.getPid() != 0 && ! selectId.contains(permissionEntity.getPid()) ) {
                    permissionEntity = allMap.get(permissionEntity.getPid()).get(0);

                    if(! permissionEntities.contains(permissionEntity)) {
                        permissionEntities.add(permissionEntity);
                    }
                }
            }
        }

        //2. 生成树
        if(! CollectionUtils.isEmpty(permissionEntities)) {
            return generateTree(permissionEntities);
        }

        return new ArrayList<>();
    }

    private List<PermissionDto> generateTree(List<PermissionEntity> permissionEntities) {

        // 1.对permissionEntities按照sort从小到大进行排序
        Collections.sort(permissionEntities, new Comparator<PermissionEntity>() {
            @Override
            public int compare(PermissionEntity p1, PermissionEntity p2) {
                return p1.getSort() - p2.getSort();
            }
        });
        // 2.转化为permissionDtoList
        List<PermissionDto> permissionDtoList = CommonBeanUtil.copyList(permissionEntities, PermissionDto.class);

        // 3.分级获取菜单
        Map<Long, List<PermissionDto>> permissionMap = permissionDtoList.stream().collect(Collectors.groupingBy(PermissionDto::getPid));

        // 4.获取根菜单
        List<PermissionDto> result = permissionMap.get(0L);
        permissionMap.remove(0L);

        // 5.循环填充根菜单
        for (PermissionDto permissionDto : result) {
            if(permissionDto.getPermissionsType().equals(0)) {
                List<PermissionDto> children = permissionMap.get(permissionDto.getId());
                //6.设置孙子节点
                if (! CollectionUtils.isEmpty(children)) {
                    children.forEach(permission -> {
                        permission.setChildren(permissionMap.get(permission.getId()));
                    });
                    permissionDto.setChildren(children);
                }
            }
        }

        return result;
    }
}
