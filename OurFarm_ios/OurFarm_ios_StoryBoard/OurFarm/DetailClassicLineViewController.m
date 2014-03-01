//
//  DeatilClassicLineViewController.m
//  OurFarm
//
//  Created by tian hao on 13-6-20.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import "DetailClassicLineViewController.h"
#import "ClassicLine.h"
#import "UIImageView+AFNetworking.h"
#import "DetailViewController.h"
#import "lineCell.h"


@interface DetailClassicLineViewController ()

@end

@implementation DetailClassicLineViewController{
    
}
@synthesize classicLine;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	[self showDetail:classicLine];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/**
 * 展示详细页面
 */
-(void) showDetail:(ClassicLine *)classicLine {
   
    //设置详细页面内容
    self.name.text = [classicLine valueForKeyPath:@"name"];
    
    self.itinerarySummary.text = [classicLine valueForKeyPath:@"itinerarySummary"];
    [self.picMap setImageWithURLRequest:[[NSURLRequest alloc] initWithURL:[NSURL URLWithString:[classicLine valueForKeyPath:@"picMap"]]]
                       placeholderImage:[UIImage imageNamed:@"home_placeholder_pic"]
                                success:^(NSURLRequest *request, NSHTTPURLResponse *response ,UIImage *image){
                                   self.picMap.image = image;
                                }
                                failure:^(NSURLRequest *request, NSHTTPURLResponse *response ,NSError *error){
                                    NSLog(@"Get picture fail in detail: %@", error);
                                }];
    
}




#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
#warning Potentially incomplete method implementation.
    
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
#warning Incomplete method implementation.
    return [classicLine.lines count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"lineCell";
    LineCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier forIndexPath:indexPath];
    
    // Configure the cell...
    if (!cell) {
        cell = [[LineCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:CellIdentifier];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        cell.textLabel.backgroundColor = [UIColor clearColor];
    }
    cell.line = [classicLine.lines objectAtIndex:indexPath.row];
    return cell;
}

#pragma mark - Table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Navigation logic may go here. Create and push another view controller.
    Line *line= [classicLine.lines objectAtIndex:indexPath.row];
    [self performSegueWithIdentifier:@"detailView" sender:line.destinationId ];
    
    NSLog(@"to detail  %@", line.destinationId);
}

/*
 * Goto detail Page
 */
-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender{
    //传递参数    
    if ([segue.identifier isEqualToString:@"detailView"]) {
        DetailViewController *detailView = [segue destinationViewController];
        [detailView setValue:sender forKey:@"destinationId"];
        NSLog(@"detail:%@", sender);
    }
}


@end
