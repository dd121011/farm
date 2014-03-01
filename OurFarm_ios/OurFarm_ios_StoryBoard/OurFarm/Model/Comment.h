//
//  Comment.h
//  OurFarm_ios
//
//  评论
//
//  Created by 李 凤勇 on 13-5-30.
//  Copyright (c) 2013年 FarmHome. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Comment : NSObject

    @property long destination_id;//内容id，景点/农家乐等内容在后台系统中的唯一标示
    @property long user_id;//评价者的用户id，匿名用户默认为0
    @property int otherSYS_type;//用户类型：0为匿名用户，1为新浪微博，2为腾讯，3微信，4为邮箱
    @property (nonatomic, copy) NSString *content;//评论内容
    @property (nonatomic, copy) NSString *comment_time;//评论时间
    @property float comment_score;//评分

@end
