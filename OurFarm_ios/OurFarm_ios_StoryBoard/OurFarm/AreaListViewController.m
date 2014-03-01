//
//  AreaListViewController.m
//  OurFarm
//
//  Created by tian hao on 13-6-28.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import "AreaListViewController.h"
#import "SummaryListAreaViewController.h"

@interface AreaListViewController ()

@end

@implementation AreaListViewController{
    @private NSMutableArray *_areaArray;
}

- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];

    [self initArea];
}

- (void)initArea{
    _areaArray = [[NSMutableArray alloc] init];
    [_areaArray addObject:[NSDictionary dictionaryWithObjectsAndKeys:@"朝阳区",@"name",@"110105",@"code",nil]];
    [_areaArray addObject:[NSDictionary dictionaryWithObjectsAndKeys:@"丰台区",@"name",@"110106",@"code",nil]];
    [_areaArray addObject:[NSDictionary dictionaryWithObjectsAndKeys:@"石景山区",@"name",@"110107",@"code",nil]];
    [_areaArray addObject:[NSDictionary dictionaryWithObjectsAndKeys:@"海淀区",@"name",@"110108",@"code",nil]];
    [_areaArray addObject:[NSDictionary dictionaryWithObjectsAndKeys:@"门头沟区",@"name",@"110109",@"code",nil]];
    [_areaArray addObject:[NSDictionary dictionaryWithObjectsAndKeys:@"房山区",@"name",@"110111",@"code",nil]];
    [_areaArray addObject:[NSDictionary dictionaryWithObjectsAndKeys:@"通州区",@"name",@"110112",@"code",nil]];
    [_areaArray addObject:[NSDictionary dictionaryWithObjectsAndKeys:@"顺义区",@"name",@"110113",@"code",nil]];
    [_areaArray addObject:[NSDictionary dictionaryWithObjectsAndKeys:@"昌平区",@"name",@"110114",@"code",nil]];
    [_areaArray addObject:[NSDictionary dictionaryWithObjectsAndKeys:@"大兴区",@"name",@"110115",@"code",nil]];
    [_areaArray addObject:[NSDictionary dictionaryWithObjectsAndKeys:@"怀柔区",@"name",@"110116",@"code",nil]];
    [_areaArray addObject:[NSDictionary dictionaryWithObjectsAndKeys:@"平谷区",@"name",@"110117",@"code",nil]];
    [_areaArray addObject:[NSDictionary dictionaryWithObjectsAndKeys:@"密云区",@"name",@"110228",@"code",nil]];
    [_areaArray addObject:[NSDictionary dictionaryWithObjectsAndKeys:@"延庆区",@"name",@"110229",@"code",nil]];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
#warning Potentially incomplete method implementation.
    // Return the number of sections.
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
#warning Incomplete method implementation.
    // Return the number of rows in the section.
    return [_areaArray count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"areaCell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier forIndexPath:indexPath];
    
    // Configure the cell...
    if (!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:CellIdentifier];
         
    }
    cell.textLabel.text = [[_areaArray objectAtIndex:indexPath.row] objectForKey:@"name"];
    return cell;
}




#pragma mark - Table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath

{
    NSLog(@"didSelectRowAtIndexPath: ");
   
    [self performSegueWithIdentifier:@"areaView" sender:  [[_areaArray objectAtIndex:indexPath.row] objectForKey:@"code"]];
    
    NSLog(@"summary %@",  [[_areaArray objectAtIndex:indexPath.row] objectForKey:@"code"]);
    
}

/*
 * Goto MapView Page
 */
-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender{
    UINavigationController *navigationController = [segue destinationViewController];
    //传递参数
    NSLog(@"prepareForSegue: "); 
    if ([segue.identifier isEqualToString:@"areaView"]) {
        SummaryListAreaViewController *areaView = [navigationController viewControllers][0];;
        if ([areaView respondsToSelector:@selector(setAreaCode:)]) {
            [areaView setValue:sender forKey:@"areaCode"];
            NSLog(@"areaCode is:%@", sender);
        }
        
    }
}


@end
