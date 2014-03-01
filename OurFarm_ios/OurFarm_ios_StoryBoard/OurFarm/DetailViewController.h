//
//  DetailViewController.h
//  OurFarm
//
//  Created by tian hao on 13-6-7.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <MapKit/MapKit.h>
#import "DestinationAnnotation.h"
#import "Destination.h"
#import "MoreAroundFarm.h"

@interface DetailViewController : UIViewController

@property Destination *destination;//请求返回的JSON转换后的对象
@property NSDictionary *json;//请求返回的JSON

@property NSNumber *lat;//纬度
@property NSNumber *lng;//经度

@property (strong) NSNumber *destinationId;//导航时到本页面时传递的参数
@property NSNumber *nearbyType;//景点的类型

@property (weak, nonatomic) IBOutlet UIBarButtonItem *like;//收藏

@property (weak, nonatomic) IBOutlet UIBarButtonItem *journey;//加入行程

@property (weak, nonatomic) IBOutlet UIBarButtonItem *share;//分享

@property (strong, nonatomic) IBOutlet UIImageView *headView;
@property (strong, nonatomic) IBOutlet UILabel *name;
@property (strong, nonatomic) IBOutlet UIView *score;

@property (strong, nonatomic) IBOutlet UILabel *priceInfo;
//@property (strong, nonatomic) IBOutlet UILabel *characteristic;
@property (strong, nonatomic) IBOutlet UIButton *address;
@property (strong, nonatomic) IBOutlet UIButton *tel;

@property (strong, nonatomic) IBOutlet UIView *viewAroundFarm;//周边农家乐整个view
@property (strong, nonatomic) IBOutlet UILabel *getAroundFarmMsg;//获取消息提示
@property (strong, nonatomic) IBOutlet UIButton *retryGetAroundFarm;//重试按钮

@property (strong, nonatomic) IBOutlet UILabel *aroundFarmLabel1;
@property (strong, nonatomic) IBOutlet UIImageView *aroundFarm1;//周边农家乐1
@property (strong, nonatomic) IBOutlet UILabel *aroundFarmLabel2;
@property (strong, nonatomic) IBOutlet UIImageView *aroundFarm2;//周边农家乐2
@property (strong, nonatomic) IBOutlet UILabel *aroundFarmLabel3;
@property (strong, nonatomic) IBOutlet UIImageView *aroundFarm3;//周边农家乐3
@property (strong, nonatomic) IBOutlet UIButton *moreFarmHome;//更多农家乐

@property (strong, nonatomic) IBOutlet UITextView *details;//详细介绍
@property (strong, nonatomic) IBOutlet UILabel *promotion;//促销、优惠信息
@property (weak, nonatomic) IBOutlet UILabel *detailName;//详细介绍的名字
@property (weak, nonatomic) IBOutlet UILabel *characteristic;//特点概要

- (IBAction)retryGetFarmHome:(id)sender;//重试按钮

- (IBAction)addFavorite:(id)sender;//添加到收藏
- (IBAction)addTrip:(id)sender;//添加到行程
- (IBAction)share:(id)sender;//分享

- (IBAction)getMoreFarmHome:(id)sender;//更多农家乐

- (IBAction)callPhone:(id)sender;//显示打电话对话框
- (IBAction)showAddressOnMap:(id)sender;//进入地图路线页面

@end
