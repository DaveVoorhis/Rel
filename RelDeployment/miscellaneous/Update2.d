insert ComposedBy ( extend DTYcomposer where DTY# = 123 : { Problem# := '095a' } ) { all but DTY# } , 
insert Problem rel { tup { Problem# '095a' , DR 6 , Month 'January' , Year '2013' , NumberOfComposers NumberOfComposers from tuple from ( DTYprob where DTY# = 123 ) } } , 
delete DTYcomposer where DTY# = 123,
update DTYprob where DTY# = 123 : { NumberOfComposers := 0 } , 
insert DTYforCP rel {tup{DTY# 123, Problem# '095a'}};